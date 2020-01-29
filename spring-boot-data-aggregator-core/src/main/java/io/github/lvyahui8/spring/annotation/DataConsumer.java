package io.github.lvyahui8.spring.annotation;

import io.github.lvyahui8.spring.enums.ExceptionProcessingMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数据依赖项 Data consumer
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:05
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataConsumer {
    /**
     * Unique identifier of the data
     */
    @AliasFor("value")
    String id() default "";

    /**
     * Same as id();
     */
    @AliasFor("id")
    String value() default "";

    /**
     * The parameter key required by the method being consumed will be dynamically replaced
     */
    DynamicParameter [] dynamicParameters() default {};

    /**
     * Exception handling, default by global configuration
     */
    ExceptionProcessingMethod exceptionProcessingMethod()
            default ExceptionProcessingMethod.BY_DEFAULT;
}
