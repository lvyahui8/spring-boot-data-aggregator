package io.github.lvyahui8.spring.aggregate.service.impl;

import io.github.lvyahui8.spring.aggregate.config.RuntimeSettings;
import io.github.lvyahui8.spring.aggregate.model.DataConsumeDefination;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefination;
import io.github.lvyahui8.spring.aggregate.model.DenpendType;
import io.github.lvyahui8.spring.aggregate.model.MethodArg;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAgregateQueryService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:50
 */
@Slf4j
public class DataBeanAgregateQueryServiceImpl implements DataBeanAgregateQueryService {

    private DataProviderRepository repository;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private ExecutorService executorService;

    @Setter
    private RuntimeSettings runtimeSettings;

    public DataBeanAgregateQueryServiceImpl(DataProviderRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> resultType) throws InterruptedException,
            InvocationTargetException, IllegalAccessException {
        Assert.isTrue(repository.contains(id),"id not exisit");
        long startTime = System.currentTimeMillis();
        DataProvideDefination provider = repository.get(id);
        Map<String,Object> dependObjectMap = new HashMap<>();
        if(provider.getDepends() != null && ! provider.getDepends().isEmpty()) {
            CountDownLatch stopDownLatch = new CountDownLatch(provider.getDepends().size());
            Map<String,Future<?>> futureMap = new HashMap<>(provider.getDepends().size());
            Map<String,DataConsumeDefination> consumeDefinationMap = new HashMap<>(provider.getDepends().size());
            for (DataConsumeDefination depend : provider.getDepends()) {
                consumeDefinationMap.put(depend.getId(),depend);
                Future<?> future = executorService.submit(() -> {
                    try {
                        Object o = get(depend.getId(), invokeParams, depend.getClazz());
                        return depend.getClazz().cast(o);
                    } finally {
                        stopDownLatch.countDown();
                    }
                });
                futureMap.put(depend.getId(),future);
            }
            stopDownLatch.await(provider.getTimeout(),TimeUnit.MILLISECONDS);
            if(! futureMap.isEmpty()){
                for (Map.Entry<String,Future<?>> item : futureMap.entrySet()) {
                    Future<?> future = item.getValue();
                    Object value = null;
                    DataConsumeDefination consumeDefination = consumeDefinationMap.get(item.getKey());
                    try {
                        value = future.get();
                    } catch (ExecutionException e) {
                        if (consumeDefination.getIgnoreException() != null ? ! consumeDefination.getIgnoreException()
                                : ! runtimeSettings.isIgnoreException()) {
                            throwException(e);
                        }
                    }
                    dependObjectMap.put(item.getKey(),value);
                }
            }
        }
        /* 拼凑dependObjects和invokeParams */
        Object [] args = new Object[provider.getMethod().getParameterCount()];
        for (int i = 0 ; i < provider.getMethodArgs().size(); i ++) {
            MethodArg methodArg = provider.getMethodArgs().get(i);
            if (methodArg.getDenpendType().equals(DenpendType.OTHER_MODEL)) {
                args[i] = dependObjectMap.get(methodArg.getAnnotionKey());
            } else {
                args[i] = invokeParams.get(methodArg.getAnnotionKey());
            }
            if (args[i] != null && ! methodArg.getParameter().getType().isAssignableFrom(args[i].getClass())) {
                throw new IllegalArgumentException("param type not match, param:"
                        + methodArg.getParameter().getName());
            }
        }
        try {
            return resultType.cast(provider.getMethod()
                    .invoke(applicationContext.getBean(provider.getMethod().getDeclaringClass()), args));
        } finally {
            logging(id, startTime, provider);
        }
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

    private void logging(String id, long startTime, DataProvideDefination provider) {
        if (runtimeSettings.isEnableLogging() && log.isInfoEnabled()) {
            log.info("query id: {}, " +
                            "costTime: {}ms, " +
                            "resultType: {},  " +
                            "invokeMethod: {}",
                    id,
                    System.currentTimeMillis() - startTime,
                    provider.getMethod().getReturnType().getSimpleName(),
                    provider.getMethod().getDeclaringClass().getSimpleName() + "#" + provider.getMethod().getName()
                    );
        }
    }

}
