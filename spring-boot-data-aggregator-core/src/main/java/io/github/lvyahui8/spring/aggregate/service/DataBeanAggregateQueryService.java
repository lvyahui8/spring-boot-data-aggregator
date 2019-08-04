package io.github.lvyahui8.spring.aggregate.service;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

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
     * @return final result
     * @throws InterruptedException 中断
     * @throws InvocationTargetException 反射异常
     * @throws IllegalAccessException 反射异常
     */
    <T> T get(String id, Map<String,Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException;


    /**
     * 并发+递归获取并拼装数据
     *
     * @param provider data Provider
     * @param invokeParams query parameters
     * @param resultType  final result type
     * @param <T> final result type
     * @return final result
     * @throws InterruptedException 中断
     * @throws InvocationTargetException 反射异常
     * @throws IllegalAccessException 反射异常
     */
    <T> T get(DataProvideDefinition provider, Map<String,Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException;

}