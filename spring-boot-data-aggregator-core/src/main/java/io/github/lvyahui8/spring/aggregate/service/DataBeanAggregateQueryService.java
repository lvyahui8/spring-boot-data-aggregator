package io.github.lvyahui8.spring.aggregate.service;

import io.github.lvyahui8.spring.aggregate.model.InvokeSign;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:49
 */
public interface DataBeanAggregateQueryService {

    /**
     * 并发+递归获取并拼装数据
     *
     * @param id  data id
     * @param invokeParams query parameters
     * @param resultType  final result type
     * @param <T> final result type
     * @param queryCache Used to cache data during the query
     * @return final result
     */
    <T> T get(String id, Map<String,Object> invokeParams, Class<T> resultType,final Map<InvokeSign,Object> queryCache)
            throws InterruptedException, InvocationTargetException, IllegalAccessException;
}