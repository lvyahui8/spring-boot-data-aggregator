package io.github.lvyahui8.spring.aggregate.func;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/13 23:19
 */
@FunctionalInterface
public interface Function2<T,U,R> extends MultipleArgumentsFunction<R> {
    /**
     * support two parameters
     *
     * @param t param 1
     * @param u param 2
     * @return return value
     */
    R apply(T t, U u);
}
