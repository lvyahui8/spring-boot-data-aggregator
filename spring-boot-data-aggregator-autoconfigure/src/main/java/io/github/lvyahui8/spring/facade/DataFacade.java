package io.github.lvyahui8.spring.facade;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2020/2/13
 */
public class DataFacade {

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    private static DataBeanAggregateQueryFacade facade;

    public static <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return facade.get(id,invokeParams,clazz);
    }

    public static <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return facade.get(invokeParams,multipleArgumentsFunction);
    }

    public static  <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction,Long timeout)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return facade.get(invokeParams,multipleArgumentsFunction,timeout);
    }

    public static <T> T groupGet(String groupKey,Map<String,Object> invokeParams,Class<T> clazz)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        throw new UnsupportedOperationException("Not yet supported");
    }
}
