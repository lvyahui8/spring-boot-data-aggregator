package io.github.lvyahui8.spring.example.aspect;

import io.github.lvyahui8.spring.example.configuration.ExampleProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/7 23:17
 */
@Slf4j
@Deprecated
public class AggregateQueryLoggingAspect {

    @Autowired
    private ExampleProperties exampleProperties;

    @Around("execution(* io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAggregateServiceImpl.get(..))")
    public Object doLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object retVal ;
        try {
            retVal = joinPoint.proceed();
        } finally {
            Object[] args = joinPoint.getArgs();
            if(log.isInfoEnabled() && exampleProperties.isLogging()) {
                log.info("query id: {}, " +
                                "costTime: {}ms, " ,
                        args[0],
                        System.currentTimeMillis() - startTime
                );
            }
        }
        return retVal;
    }
}
