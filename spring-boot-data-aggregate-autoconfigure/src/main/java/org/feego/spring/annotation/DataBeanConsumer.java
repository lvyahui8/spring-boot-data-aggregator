package org.feego.spring.annotation;

import java.lang.annotation.*;

/**
 * 数据依赖项
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:05
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataBeanConsumer {
    String id();
}
