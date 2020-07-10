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
public interface DataAggregateQueryFacade {
    /**
     * See also {@link DataQueryStaticFacade#get(String, Map, Class)}
     */
    <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz);

    /**
     * See also {@link DataQueryStaticFacade#get(Map, MultipleArgumentsFunction)}
     */
    <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction);

    /**
     * See also {@link DataQueryStaticFacade#get(Map, MultipleArgumentsFunction, Long)}
     */
    <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction,Long timeout);
}
