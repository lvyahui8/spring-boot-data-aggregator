package io.github.lvyahui8.spring.example.configuration;

import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptorChain;
import io.github.lvyahui8.spring.aggregate.interceptor.impl.AggregateQueryInterceptorChainImpl;
import io.github.lvyahui8.spring.example.interceptor.SampleAggregateQueryInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/8/4 16:14
 */
@Configuration
public class AggregatorCustomConfiguration {
    /**
     * 自定义ExecutorService, 替代aggregator库使用的executorService
     * @return
     */
    @Bean
    public ExecutorService aggregateExecutorService() {
        return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),Runtime.getRuntime().availableProcessors() * 2 ,
                2L, TimeUnit.HOURS,
                new LinkedBlockingDeque<>(1024),
                new CustomizableThreadFactory("example-async"));
    }

    /**
     * 自定义拦截器处理链
     *
     * @param sampleAggregateQueryInterceptor
     * @return
     */
    @Bean(name = "aggregateQueryInterceptorChain")
    public AggregateQueryInterceptorChain aggregateQueryInterceptorChain(
            @Qualifier("sampleAggregateQueryInterceptor") SampleAggregateQueryInterceptor sampleAggregateQueryInterceptor) {
        AggregateQueryInterceptorChainImpl aggregateQueryInterceptorChain = new AggregateQueryInterceptorChainImpl();
        aggregateQueryInterceptorChain.addInterceptor(sampleAggregateQueryInterceptor);
        return aggregateQueryInterceptorChain;
    }
}
