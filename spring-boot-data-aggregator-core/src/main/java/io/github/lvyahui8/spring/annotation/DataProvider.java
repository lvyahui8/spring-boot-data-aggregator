package io.github.lvyahui8.spring.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数据提供者 Data provider
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataProvider {

    /**
     * Unique identifier of the data
     */
    @AliasFor("value")
    String id() default "";

    /**
     * Same as id()
     */
    @AliasFor("id")
    String value() default "";

    /**
     * Asynchronous execution method timeout
     */
    long timeout() default -1;

    /**
     * The call to this data providing method should be idempotent,
     * which determines whether its execution result will be cached.
     */
    boolean idempotent() default true;
}
