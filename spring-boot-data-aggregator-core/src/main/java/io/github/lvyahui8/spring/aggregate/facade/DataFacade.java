package io.github.lvyahui8.spring.aggregate.facade;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2020/2/13
 */
public class DataFacade {


    private static DataBeanAggregateQueryFacade facade;

    static void setFacade(DataBeanAggregateQueryFacade facade) {
        DataFacade.facade = facade;
    }

    static DataBeanAggregateQueryFacade getFacade() {
        return facade;
    }

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

}
