package io.github.lvyahui8.spring.aggregate.service.impl;

import io.github.lvyahui8.spring.aggregate.config.RuntimeSettings;
import io.github.lvyahui8.spring.aggregate.consts.AggregatorConstant;
import io.github.lvyahui8.spring.aggregate.model.*;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateQueryService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

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

    private final DataProviderRepository repository;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private ExecutorService executorService;

    @Setter
    private RuntimeSettings runtimeSettings;

    public DataBeanAggregateQueryServiceImpl(DataProviderRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> resultType,final Map<InvokeSignature,Object> queryCache)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Assert.isTrue(repository.contains(id),"id not exist");
        DataProvideDefinition provider = repository.get(id);
        return get(provider,invokeParams,resultType,queryCache);
    }

    @Override
    public <T> T get(DataProvideDefinition provider, Map<String, Object> invokeParams, Class<T> resultType,
                     Map<InvokeSignature, Object> queryCache)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Map<String,Object> dependObjectMap = null;
        if(provider.getDepends() != null && ! provider.getDepends().isEmpty()) {
            List<DataConsumeDefinition> consumeDefinitions = provider.getDepends();
            Long timeout = provider.getTimeout() != null ? provider.getTimeout() : runtimeSettings.getTimeout();
            dependObjectMap = getDependObjectMap(invokeParams, consumeDefinitions, timeout, queryCache);
        } else {
            dependObjectMap = Collections.emptyMap();
        }
        /* 拼凑dependObjects和invokeParams */
        Object [] args = new Object[provider.getMethod().getParameterCount()];
        for (int i = 0 ; i < provider.getMethodArgs().size(); i ++) {
            MethodArg methodArg = provider.getMethodArgs().get(i);
            if (methodArg.getDependType().equals(DependType.OTHER_MODEL)) {
                args[i] = dependObjectMap.get(methodArg.getAnnotationKey());
            } else {
                args[i] = invokeParams.get(methodArg.getAnnotationKey());
            }
            if (args[i] != null && ! methodArg.getParameter().getType().isAssignableFrom(args[i].getClass())) {
                throw new IllegalArgumentException("param type not match, param:"
                        + methodArg.getParameter().getName());
            }
        }

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
                queryCache.put(invokeSignature,resultModel != null ? resultModel : AggregatorConstant.EMPTY_MODEL);
            }
        }

        return resultType.cast(resultModel != AggregatorConstant.EMPTY_MODEL ? resultModel : null);
    }

    @Override
    public Map<String, Object> getDependObjectMap(Map<String, Object> invokeParams,
                                                  List<DataConsumeDefinition> consumeDefinitions,
                                                  Long timeout,
                                                  Map<InvokeSignature, Object> queryCache)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> dependObjectMap;
        CountDownLatch stopDownLatch = new CountDownLatch(consumeDefinitions.size());
        Map<String, Future<?>> futureMap = new HashMap<>(consumeDefinitions.size());
        dependObjectMap = new HashMap<>(consumeDefinitions.size());
        Map<String,DataConsumeDefinition> consumeDefinitionMap = new HashMap<>(consumeDefinitions.size());
        for (DataConsumeDefinition depend : consumeDefinitions) {
            consumeDefinitionMap.put(depend.getId(),depend);
            Future<?> future = executorService.submit(() -> {
                try {
                    Object o = get(depend.getId(), invokeParams, depend.getClazz(),queryCache);
                    return depend.getClazz().cast(o);
                } finally {
                    stopDownLatch.countDown();
                }
            });
            futureMap.put(depend.getId(),future);
        }
        stopDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        if(! futureMap.isEmpty()){
            for (Map.Entry<String,Future<?>> item : futureMap.entrySet()) {
                Future<?> future = item.getValue();
                Object value = null;
                DataConsumeDefinition consumeDefinition = consumeDefinitionMap.get(item.getKey());
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
        if (e.getCause() instanceof InterruptedException) {
            throw (InterruptedException) e.getCause();
        } else if (e.getCause() instanceof  InvocationTargetException){
            throw (InvocationTargetException) e.getCause();
        } else if (e.getCause() instanceof IllegalAccessException) {
            throw (IllegalAccessException) e.getCause();
        } else {
            throw (RuntimeException) e.getCause();
        }
    }

}
