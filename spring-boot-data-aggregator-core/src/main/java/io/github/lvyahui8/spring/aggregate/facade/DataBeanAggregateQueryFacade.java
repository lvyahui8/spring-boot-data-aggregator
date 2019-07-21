package io.github.lvyahui8.spring.aggregate.facade;

import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * The only API for this framework
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:22
 */
public interface DataBeanAggregateQueryFacade {
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
     * @throws InterruptedException  If the thread is interrupted, this exception will be thrown
     * @throws IllegalAccessException Thrown if the data provider cannot be executed
     * @throws InvocationTargetException If the data provider throws an unhandled exception, this exception will be thrown
     */
    <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz)
            throws InterruptedException, IllegalAccessException, InvocationTargetException;

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
     * @throws InterruptedException  If the thread is interrupted, this exception will be thrown
     * @throws IllegalAccessException Thrown if the data provider cannot be executed
     * @throws InvocationTargetException If the data provider throws an unhandled exception, this exception will be thrown
     */
    <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction)
            throws InterruptedException, IllegalAccessException, InvocationTargetException;

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
     * @throws InterruptedException  If the thread is interrupted, this exception will be thrown
     * @throws IllegalAccessException Thrown if the data provider cannot be executed
     * @throws InvocationTargetException If the data provider throws an unhandled exception, this exception will be thrown
     */
    <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction,Long timeout)
            throws InterruptedException, IllegalAccessException, InvocationTargetException;
}
