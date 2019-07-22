package io.github.lvyahui8.spring.aggregate.facade.impl;

import io.github.lvyahui8.spring.aggregate.consts.AggregationConstant;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateQueryService;
import io.github.lvyahui8.spring.aggregate.util.DefinitionUtils;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:22
 */
public class DataBeanAggregateQueryFacadeImpl implements DataBeanAggregateQueryFacade {

    private final DataBeanAggregateQueryService dataBeanAggregateQueryService;

    public DataBeanAggregateQueryFacadeImpl(DataBeanAggregateQueryService dataBeanAggregateQueryService) {
        this.dataBeanAggregateQueryService = dataBeanAggregateQueryService;
    }

    @Override
    public <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        Assert.notNull(id,"id must be not null!");
        Assert.notNull(clazz,"clazz must be not null !");
        if(invokeParams == null) {
            invokeParams = Collections.emptyMap();
        }
        return dataBeanAggregateQueryService.get(id,invokeParams,clazz,
                new ConcurrentHashMap<>(AggregationConstant.DEFAULT_INITIAL_CAPACITY));
    }

    @Override
    public <T> T get(Map<String, Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return get(invokeParams,multipleArgumentsFunction,null);
    }

    @Override
    public <T> T get(Map<String, Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)  throws InterruptedException, IllegalAccessException, InvocationTargetException{
        Method[] methods = multipleArgumentsFunction.getClass().getMethods();
        Method applyMethod = null;
        if(invokeParams == null) {
            invokeParams = Collections.emptyMap();
        }

        for (Method method : methods) {
            if(! Modifier.isStatic(method.getModifiers()) && ! method.isDefault()) {
                applyMethod = method;
                break;
            }
        }

        if(applyMethod == null) {
            throw new IllegalAccessException(multipleArgumentsFunction.getClass().getName());
        }


        DataProvideDefinition provider = DefinitionUtils.getProvideDefinition(applyMethod);
        provider.setTimeout(timeout);
        provider.setTarget(multipleArgumentsFunction);

        boolean accessible = applyMethod.isAccessible();
        if(! accessible) {
            applyMethod.setAccessible(true);
        }
        try {
            @SuppressWarnings("unchecked")
            T ret = (T) dataBeanAggregateQueryService.get(provider, invokeParams, applyMethod.getReturnType(),
                    new ConcurrentHashMap<>(AggregationConstant.DEFAULT_INITIAL_CAPACITY));

            return ret;
        } finally {
            if(! accessible) {
                applyMethod.setAccessible(accessible);
            }
        }
    }
}
