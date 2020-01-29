package io.github.lvyahui8.spring.example.interceptor;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptor;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/8/4 17:24
 */
@Component
@Order(2)
@Slf4j
public class SampleAggregateQueryInterceptor implements AggregateQueryInterceptor {
    @Override
    public boolean querySubmitted(AggregationContext aggregationContext) {
        log.info("begin query. root:{}",aggregationContext.getRootProvideDefinition().getMethod().getName());
        return true;
    }

    @Override
    public void queryBefore(AggregationContext aggregationContext, DataProvideDefinition provideDefinition) {
        log.info("query before. provider:{}",provideDefinition.getMethod().getName());
    }

    @Override
    public Object queryAfter(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Object result) {
        log.info("query after. provider:{},result:{}",provideDefinition.getMethod().getName(),result != null ?
                result.toString() : "null");
        return result;
    }

    @Override
    public void exceptionHandle(AggregationContext aggregationContext, DataProvideDefinition provideDefinition, Exception e) {
        log.error(e.getMessage());
    }

    @Override
    public void queryFinished(AggregationContext aggregationContext) {
        log.info("query finish. root: {}",aggregationContext.getRootProvideDefinition().getMethod().getName());
    }
}
