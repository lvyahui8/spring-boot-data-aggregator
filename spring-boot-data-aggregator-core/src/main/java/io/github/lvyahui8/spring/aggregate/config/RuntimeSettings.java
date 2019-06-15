package io.github.lvyahui8.spring.aggregate.config;

import lombok.Data;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/15 2:46
 */
@Data
public class RuntimeSettings {
    private boolean enableLogging;
    private boolean ignoreException;
}
