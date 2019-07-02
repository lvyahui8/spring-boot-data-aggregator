package io.github.lvyahui8.spring.aggregate.model;

/**
 * The dependency type of the parameter of the data provider's method
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/3 22:44
 */
public enum DependType {
    /**
     * Caller passed parameters
     */
    INVOKE_PARAM,
    /**
     * Parameters that require automatic injection
     */
    OTHER_MODEL
}
