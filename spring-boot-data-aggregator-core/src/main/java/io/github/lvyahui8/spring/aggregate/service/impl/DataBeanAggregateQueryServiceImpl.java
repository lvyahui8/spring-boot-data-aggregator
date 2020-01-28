package io.github.lvyahui8.spring.aggregate.service.impl;

import io.github.lvyahui8.spring.aggregate.config.RuntimeSettings;
import io.github.lvyahui8.spring.aggregate.consts.AggregationConstant;
import io.github.lvyahui8.spring.aggregate.context.AggregationContext;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptorChain;
import io.github.lvyahui8.spring.aggregate.model.*;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.service.AbstractAsyncQueryTask;
import io.github.lvyahui8.spring.aggregate.service.AsyncQueryTaskWrapper;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateQueryService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:50
 */
@Slf4j
public class DataBeanAggregateQueryServiceImpl implements DataBeanAggregateQueryService {

    @Setter
    private DataProviderRepository repository;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private ExecutorService executorService;

    @Setter
    private RuntimeSettings runtimeSettings;

    @Setter
    private AggregateQueryInterceptorChain interceptorChain;

    @Setter
    private Class<? extends AsyncQueryTaskWrapper> taskWrapperClazz;

    private AggregationContext initQueryContext(DataProvideDefinition rootProvider, Map<InvokeSignature,Object> queryCache) {
        AggregationContext aggregationContext = new AggregationContext();
        aggregationContext.setRootThread(Thread.currentThread());
        aggregationContext.setRootProvideDefinition(rootProvider);
        aggregationContext.setCacheMap(queryCache);
        return aggregationContext;
    }

    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        return get(repository.get(id),invokeParams,resultType);
    }

    @Override
    public <T> T get(DataProvideDefinition provider, Map<String, Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Map<InvokeSignature, Object> queryCache = new ConcurrentHashMap<>(AggregationConstant.DEFAULT_INITIAL_CAPACITY);
        AggregationContext aggregationContext = initQueryContext(provider, queryCache);
        interceptorChain.applyQuerySubmitted(aggregationContext);
        try {
            return innerGet(provider,invokeParams,resultType,aggregationContext,null);
        } finally {
            interceptorChain.applyQueryFinished(aggregationContext);
        }
    }

    private  <T> T innerGet(DataProvideDefinition provider, Map<String, Object> invokeParams, Class<T> resultType,
                     AggregationContext context, DataConsumeDefinition causedConsumer)
            throws InterruptedException, InvocationTargetException, IllegalAccessException{
        Map<String,Object> dependObjectMap;
        try {
            interceptorChain.applyQueryBefore(context,provider);
            if(provider.getDepends() != null && ! provider.getDepends().isEmpty()) {
                List<DataConsumeDefinition> consumeDefinitions = provider.getDepends();
                Long timeout = provider.getTimeout() != null ? provider.getTimeout() : runtimeSettings.getTimeout();
                dependObjectMap = getDependObjectMap(invokeParams, consumeDefinitions, timeout, context);
            } else {
                dependObjectMap = Collections.emptyMap();
            }
            /* 拼凑dependObjects和invokeParams */
            Object [] args = new Object[provider.getMethod().getParameterCount()];

            for (int i = 0 ; i < provider.getMethodArgs().size(); i ++) {
                MethodArg methodArg = provider.getMethodArgs().get(i);
                if (methodArg.getDependType().equals(DependType.OTHER_MODEL)) {
                    args[i] = dependObjectMap.get(methodArg.getAnnotationKey() + "_" + methodArg.getParameter().getName());
                } else {
                    String paramKey ;
                    if(causedConsumer != null && causedConsumer.getDynamicParameterKeyMap() != null
                    && causedConsumer.getDynamicParameterKeyMap().containsKey(methodArg.getAnnotationKey())) {
                        paramKey = causedConsumer.getDynamicParameterKeyMap().get(methodArg.getAnnotationKey());
                    } else {
                        paramKey = methodArg.getAnnotationKey();
                    }
                    args[i] = invokeParams.get(paramKey);
                }
                if (args[i] != null && ! methodArg.getParameter().getType().isAssignableFrom(args[i].getClass())) {
                    throw new IllegalArgumentException("param type not match, param:"
                            + methodArg.getParameter().getName());
                }
            }
            Map<InvokeSignature, Object> queryCache = context.getCacheMap();
            /* 如果调用方法是幂等的, 那么当方法签名和方法参数完全一致时, 可以直接使用缓存结果 */
            InvokeSignature invokeSignature = new InvokeSignature(provider.getMethod(),args);
            Object resultModel;
            if(provider.isIdempotent() && queryCache.containsKey(invokeSignature)) {
                resultModel = queryCache.get(invokeSignature);
            }
            else {
                resultModel = provider.getMethod()
                        .invoke(provider.getTarget() == null
                                ? applicationContext.getBean(provider.getMethod().getDeclaringClass())
                                : provider.getTarget(), args);
                if(provider.isIdempotent()) {
                    /* Map 中可能不能放空value */
                    queryCache.put(invokeSignature,resultModel != null ? resultModel : AggregationConstant.EMPTY_MODEL);
                }
            }
            Object result = resultModel != AggregationConstant.EMPTY_MODEL ? resultModel : null;
            return resultType.cast(interceptorChain.applyQueryAfter(context,provider,result));
        } catch (Exception e) {
            interceptorChain.applyExceptionHandle(context,provider,e);
            throw e;
        }
    }

    private Map<String, Object> getDependObjectMap(Map<String, Object> invokeParams,
                                                  List<DataConsumeDefinition> consumeDefinitions,
                                                  Long timeout,
                                                  AggregationContext context)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> dependObjectMap;
        CountDownLatch stopDownLatch = new CountDownLatch(consumeDefinitions.size());
        Map<String, Future<?>> futureMap = new HashMap<>(consumeDefinitions.size());
        dependObjectMap = new HashMap<>(consumeDefinitions.size());
        Map<String,DataConsumeDefinition> consumeDefinitionMap = new HashMap<>(consumeDefinitions.size());
        for (DataConsumeDefinition depend : consumeDefinitions) {
            consumeDefinitionMap.put(depend.getId(),depend);
            AsyncQueryTaskWrapper taskWrapper;
            try {
                taskWrapper = taskWrapperClazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("task wrapper instance create failed.",e);
            }
            taskWrapper.beforeSubmit();
            Future<?> future = executorService.submit(new AbstractAsyncQueryTask<Object>(Thread.currentThread(),taskWrapper) {
                @Override
                public Object execute() throws Exception {
                    try {
                        Object o = innerGet(repository.get(depend.getId()),invokeParams, depend.getClazz(),context,depend);
                        return depend.getClazz().cast(o);
                    } finally {
                        stopDownLatch.countDown();
                    }
                }
            });
            futureMap.put(depend.getId() + "_" + depend.getOriginalParameterName(),future);
        }
        stopDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        if(! futureMap.isEmpty()){
            for (Map.Entry<String,Future<?>> item : futureMap.entrySet()) {
                Future<?> future = item.getValue();
                Object value = null;
                DataConsumeDefinition consumeDefinition = consumeDefinitionMap.get(item.getKey().substring(0,item.getKey().indexOf('_')));
                try {
                    value = future.get();
                } catch (ExecutionException e) {
                    if (consumeDefinition.getIgnoreException() != null ? ! consumeDefinition.getIgnoreException()
                            : ! runtimeSettings.isIgnoreException()) {
                        throwException(e);
                    }
                }
                dependObjectMap.put(item.getKey(),value);
            }
        }
        return dependObjectMap;
    }

    private void throwException(ExecutionException e)  throws InterruptedException,
            InvocationTargetException, IllegalAccessException  {
        Throwable cause = e.getCause();
        if (cause instanceof InterruptedException) {
            throw (InterruptedException) cause;
        } else if (cause instanceof  InvocationTargetException){
            throw (InvocationTargetException) cause;
        } else if (cause instanceof IllegalAccessException) {
            throw (IllegalAccessException) cause;
        } else {
            throw (RuntimeException) cause;
        }
    }

}
