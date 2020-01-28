package io.github.lvyahui8.spring.aggregate.facade.impl;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateService;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;


/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:22
 */
public class DataBeanAggregateQueryFacadeImpl implements DataBeanAggregateQueryFacade {

    private final DataBeanAggregateService dataBeanAggregateService;

    public DataBeanAggregateQueryFacadeImpl(DataBeanAggregateService dataBeanAggregateService) {
        this.dataBeanAggregateService = dataBeanAggregateService;
    }

    @Override
    public <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        Assert.notNull(id,"id must be not null!");
        Assert.notNull(clazz,"clazz must be not null !");
        if(invokeParams == null) {
            invokeParams = Collections.emptyMap();
        }
        return dataBeanAggregateService.get(id,invokeParams,clazz);
    }

    @Override
    public <T> T get(Map<String, Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return get(invokeParams,multipleArgumentsFunction,null);
    }

    @Override
    public <T> T get(Map<String, Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)  throws InterruptedException, IllegalAccessException, InvocationTargetException{
        if(invokeParams == null) {
            invokeParams = Collections.emptyMap();
        }

        DataProvideDefinition provider = dataBeanAggregateService.getProvider(multipleArgumentsFunction);
        Method applyMethod = provider.getMethod();
        boolean accessible = applyMethod.isAccessible();
        if(! accessible) {
            applyMethod.setAccessible(true);
        }
        try {
            @SuppressWarnings("unchecked")
            T ret = (T) dataBeanAggregateService.get(provider, invokeParams, applyMethod.getReturnType());

            return ret;
        } finally {
            if(! accessible) {
                applyMethod.setAccessible(accessible);
            }
        }
    }
}
