package io.github.lvyahui8.spring.annotation;

import java.lang.annotation.*;

/**
 * 数据聚合时需要的输入参数
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:32
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvokeParameter {
    /**
     * Manually passed parameter key
     */
    String value();
}
