package io.github.lvyahui8.spring.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/7 23:17
 */
@Aspect
@Component
@Slf4j
public class AggregateQueryLoggingAspect {

    public AggregateQueryLoggingAspect() {
        log.info("init aspect bean");
    }

    @Pointcut("execution(* io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAggregateQueryServiceImpl.*(..))")
    public void aggregateQuery() {}

    @Around("execution(* io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAggregateQueryServiceImpl.*(..))")
    public Object doLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        Object retVal ;
        try {
            retVal = joinPoint.proceed();
        } finally {
            log.info("x");
        }
        return retVal;
    }
}
