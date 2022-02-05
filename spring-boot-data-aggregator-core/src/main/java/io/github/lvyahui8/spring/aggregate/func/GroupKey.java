package io.github.lvyahui8.spring.aggregate.func;

import java.lang.annotation.*;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/2/5
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroupKey {
    String value();
}
