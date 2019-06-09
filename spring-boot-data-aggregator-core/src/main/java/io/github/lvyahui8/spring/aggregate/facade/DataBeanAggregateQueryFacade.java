package io.github.lvyahui8.spring.aggregate.facade;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:22
 */
public interface DataBeanAggregateQueryFacade {
    <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz) throws InterruptedException, IllegalAccessException, InvocationTargetException;
}
