package io.github.lvyahui8.spring.annotation;

import java.lang.annotation.*;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/11/6 23:22
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicParameter {
    /**
     *  Method originally required parameter key.
     */
    String targetKey();

    /**
     * The new key used to replace the original parameter key
     */
    String replacementKey();
}
