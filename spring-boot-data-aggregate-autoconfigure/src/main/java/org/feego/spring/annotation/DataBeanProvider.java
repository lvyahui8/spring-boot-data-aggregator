package org.feego.spring.annotation;

import java.lang.annotation.*;

/**
 * 数据提供者
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:05
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataBeanProvider {
    String id();
    long timeout() default 1000;
}
