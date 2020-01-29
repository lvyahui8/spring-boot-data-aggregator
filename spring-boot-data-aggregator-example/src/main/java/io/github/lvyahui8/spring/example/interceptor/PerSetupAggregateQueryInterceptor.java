package io.github.lvyahui8.spring.example.interceptor;

import io.github.lvyahui8.spring.aggregate.context.AggregationContext;
import io.github.lvyahui8.spring.aggregate.interceptor.impl.AggregateQueryInterceptorAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/9/7 23:02
 */
@Component
@Order(1)
@Slf4j
public class PerSetupAggregateQueryInterceptor extends AggregateQueryInterceptorAdapter {
    @Override
    public boolean querySubmitted(AggregationContext aggregationContext) {
        log.info("current thread {}", Thread.currentThread().getName());
        return super.querySubmitted(aggregationContext);
    }
}
