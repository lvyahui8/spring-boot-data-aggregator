package io.github.lvyahui8.spring.aggregate.interceptor;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/8/4 15:10
 */
public interface AggregateQueryInterceptorChain {
    /**
     * 新增一个拦截器
     *
     * @param queryInterceptor 新增的拦截器
     */
    void addInterceptor(AggregateQueryInterceptor queryInterceptor) ;

    /**
     * 顺序调用拦截器前置处理, 查询正常提交, Context已经创建
     *
     * @param aggregationContext 查询上下文
     * @return 是否提前结束
     */
    boolean applyQuerySubmitted(AggregationContext aggregationContext);
    /**
     * 顺序调用拦截器前置处理, 每个Provider方法执行前, 将调用此方法. 存在并发调用
     *
     * @param aggregationContext 查询上下文
     * @param provideDefinition 将被执行的Provider
     */
    void applyQueryBefore(AggregationContext aggregationContext, DataProvideDefinition provideDefinition);

    /**
     * 顺序调用拦截器前置处理, 每个Provider方法执行成功之后, 调用此方法. 存在并发调用
     *  @param aggregationContext 查询上下文
     * @param provideDefinition 被执行的Provider
     * @param result 查询结果
     * @return 链式处理后的结果
     */
    Object applyQueryAfter(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Object result);

    /**
     * 顺序调用拦截器前置处理, 每个Provider执行时, 如果抛出异常, 将调用此方法. 存在并发调用
     *
     * @param aggregationContext  查询上下文
     * @param provideDefinition 被执行的Provider
     * @param e Provider抛出的异常
     */
    void applyExceptionHandle(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Exception e);

    /**
     * 顺序调用拦截器前置处理, 一次查询全部完成.
     *
     * @param aggregationContext 查询上下文
     */
    void applyQueryFinished(AggregationContext aggregationContext);
}
