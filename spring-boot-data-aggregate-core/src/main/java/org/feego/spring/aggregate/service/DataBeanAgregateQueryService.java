package org.feego.spring.aggregate.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:49
 */
public interface DataBeanAgregateQueryService {
    <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz) throws InterruptedException, InvocationTargetException, IllegalAccessException;
}
