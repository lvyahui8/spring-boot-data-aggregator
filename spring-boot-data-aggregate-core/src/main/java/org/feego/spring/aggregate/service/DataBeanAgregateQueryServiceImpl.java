package org.feego.spring.aggregate.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.feego.spring.aggregate.model.DataDepend;
import org.feego.spring.aggregate.model.DataProvider;
import org.feego.spring.aggregate.model.DenpendType;
import org.feego.spring.aggregate.model.MethodArg;
import org.feego.spring.aggregate.repository.DataProviderRepository;
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

    public DataBeanAgregateQueryServiceImpl(DataProviderRepository repository) {
        this.repository = repository;
    }

    /**
     * 并发+递归获取并拼装数据
     *
     * @param id
     * @param invokeParams
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> clazz) throws InterruptedException,
            InvocationTargetException, IllegalAccessException {
        Assert.isTrue(repository.contains(id),"id not exisit");
        long startTime = System.currentTimeMillis();
        DataProvider provider = repository.get(id);
        Map<String,Object> dependObjectMap = new HashMap<>();
        if(provider.getDepends() != null && ! provider.getDepends().isEmpty()) {
            CountDownLatch stopDownLatch = new CountDownLatch(provider.getDepends().size());
            Map<String,Future<?>> futureMap = new HashMap<>(provider.getDepends().size());
            for (DataDepend depend : provider.getDepends()) {
                Future<?> future = executorService.submit(() -> {
                    try {
                        Object o = get(depend.getId(), invokeParams, depend.getClazz());
                        return depend.getClazz().cast(o);
                    } catch (Exception e) {
                        return null;
                    }finally {
                        stopDownLatch.countDown();
                    }
                });
                futureMap.put(depend.getId(),future);
            }
            stopDownLatch.await(provider.getTimeout(),TimeUnit.MILLISECONDS);
            if(! futureMap.isEmpty()){
                for (Map.Entry<String,Future<?>> item : futureMap.entrySet()) {
                    try {
                        dependObjectMap.put(item.getKey(),item.getValue().get());
                    } catch (ExecutionException e) {
                        //
                    }
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
            if (! methodArg.getParameter().getType().isAssignableFrom(args[i].getClass())) {
                throw new IllegalArgumentException("param type not match, param:"
                        + methodArg.getParameter().getName());
            }
        }

        T result = clazz.cast(provider.getMethod()
                .invoke(applicationContext.getBean(provider.getMethod().getDeclaringClass()), args));
        if (log.isInfoEnabled()) {
            log.info("query id: {}, costTime: {}ms, model: {}, params: {}",id,
                    System.currentTimeMillis() - startTime,
                    provider.getMethod().getReturnType().getSimpleName(),
                    StringUtils.join(args));
        }
        return result;
    }

}
