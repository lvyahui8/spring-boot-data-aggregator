package io.github.lvyahui8.spring.aggregate.func;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/2/5
 */
public interface FunctionGroup {
    Object apply(AggregationContext context);
}
