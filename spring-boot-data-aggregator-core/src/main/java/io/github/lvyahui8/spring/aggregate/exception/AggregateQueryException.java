package io.github.lvyahui8.spring.aggregate.exception;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2020/7/10
 */
public class AggregateQueryException extends RuntimeException {
    public AggregateQueryException(Throwable cause) {
        super(cause);
    }
}
