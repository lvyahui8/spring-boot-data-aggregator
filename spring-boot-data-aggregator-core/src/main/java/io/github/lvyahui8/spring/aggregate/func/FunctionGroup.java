package io.github.lvyahui8.spring.aggregate.func;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;

/**
 *
 * 一组类实现此接口，并注解上groupKey以并发调用
 *
 * @author feego lvyahui8@gmail.com
 * @date 2022/2/5
 */
public interface FunctionGroup {
    Object apply(AggregationContext context);
}
