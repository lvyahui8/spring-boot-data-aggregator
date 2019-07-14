package io.github.lvyahui8.spring.enums;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/15 23:13
 */
public enum ExceptionProcessingMethod {
    /**
     * Ignore exception thrown by asynchronous execution, method returns null value
     */
    IGNORE,
    /**
     * Follow the default handling of the framework
     */
    BY_DEFAULT,
    /**
     * Abort execution
     */
    SUSPEND
}
