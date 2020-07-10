package io.github.lvyahui8.spring.aggregate.facade;

import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2020/2/13
 */
public class DataQueryStaticFacade {

    private static DataAggregateQueryFacade facade;

    static void setFacade(DataAggregateQueryFacade facade) {
        DataQueryStaticFacade.facade = facade;
    }

    static DataAggregateQueryFacade getFacade() {
        return facade;
    }

    /**
     * Used to query data. It has the following three functions
     * 1. Automatic analysis dependence
     * 2. Parallel access dependency
     * 3. Automatic injection
     *
     * @param id Data id
     * @param invokeParams Fixed parameters that need to be passed in the query process
     * @param clazz Return value type bytecode object
     * @param <T> Return value type
     * @return Return value
     */
    public static <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz) {
        return facade.get(id,invokeParams,clazz);
    }

    /**
     * Used to query data. It has the following three functions
     * 1. Automatic analysis dependence
     * 2. Parallel access dependency
     * 3. Automatic injection
     *
     * @param invokeParams Fixed parameters that need to be passed in the query process
     * @param multipleArgumentsFunction Multiple arguments function
     * @param <T> Return value type
     * @return Return value
     */
    public static <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction) {
        return facade.get(invokeParams,multipleArgumentsFunction);
    }

    /**
     * Used to query data. It has the following three functions
     * 1. Automatic analysis dependence
     * 2. Parallel access dependency
     * 3. Automatic injection
     *
     * @param invokeParams Fixed parameters that need to be passed in the query process
     * @param multipleArgumentsFunction Multiple arguments function
     * @param timeout Timeout
     * @param <T> Return value type
     * @return Return value
     */
    public static  <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction,Long timeout) {
        return facade.get(invokeParams,multipleArgumentsFunction,timeout);
    }

}
