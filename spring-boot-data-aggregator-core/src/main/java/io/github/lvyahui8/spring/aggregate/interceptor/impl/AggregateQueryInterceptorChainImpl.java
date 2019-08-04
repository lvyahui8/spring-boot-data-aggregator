package io.github.lvyahui8.spring.aggregate.interceptor.impl;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptor;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptorChain;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/8/4 15:22
 */
public class AggregateQueryInterceptorChainImpl implements AggregateQueryInterceptorChain {

    private List<AggregateQueryInterceptor> interceptors;

    private List<AggregateQueryInterceptor> initInterceptors() {
        if(this.interceptors == null) {
            synchronized (this) {
                if(this.interceptors == null) {
                    this.interceptors = new ArrayList<>(1);
                }
            }
        }
        return this.interceptors;
    }

    @Override
    public void addInterceptor(AggregateQueryInterceptor queryInterceptor) {
        this.initInterceptors().add(queryInterceptor);
    }

    @Override
    public boolean applyQuerySubmitted(AggregationContext aggregationContext) {
        for (AggregateQueryInterceptor interceptor : this.initInterceptors()) {
            if(!interceptor.querySubmitted(aggregationContext)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void applyQueryBefore(AggregationContext aggregationContext, DataProvideDefinition provideDefinition) {
        for (AggregateQueryInterceptor interceptor : this.initInterceptors()) {
            interceptor.queryBefore(aggregationContext,provideDefinition);
        }
    }

    @Override
    public Object applyQueryAfter(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Object result) {
        for (AggregateQueryInterceptor interceptor : this.initInterceptors()) {
            result = interceptor.queryAfter(aggregationContext,provideDefinition,result);
        }
        return result;
    }

    @Override
    public void applyExceptionHandle(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Exception e) {
        for (AggregateQueryInterceptor interceptor : this.initInterceptors()) {
            interceptor.exceptionHandle(aggregationContext,provideDefinition,e);
        }
    }

    @Override
    public void applyQueryFinished(AggregationContext aggregationContext) {
        for (AggregateQueryInterceptor interceptor : this.initInterceptors()) {
            interceptor.queryFinished(aggregationContext);
        }
    }
}
