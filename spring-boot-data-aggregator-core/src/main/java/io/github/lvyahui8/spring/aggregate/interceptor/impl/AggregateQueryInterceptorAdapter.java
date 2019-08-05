package io.github.lvyahui8.spring.aggregate.interceptor.impl;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptor;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/8/4 15:16
 */
public class AggregateQueryInterceptorAdapter implements AggregateQueryInterceptor {
    @Override
    public boolean querySubmitted(AggregationContext aggregationContext) {
        return true;
    }

    @Override
    public void queryBefore(AggregationContext aggregationContext, DataProvideDefinition provideDefinition) {

    }

    @Override
    public Object queryAfter(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Object result) {
        return result;
    }

    @Override
    public void exceptionHandle(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Exception e) {

    }

    @Override
    public void queryFinished(AggregationContext aggregationContext) {

    }
}
