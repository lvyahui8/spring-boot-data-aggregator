package io.github.lvyahui8.spring.aggregate2;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/2
 */
public class StrongDependentException extends RuntimeException {
    public StrongDependentException(Throwable cause) {
        super(cause);
    }
}
