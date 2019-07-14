package io.github.lvyahui8.spring.aggregate.func;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/14 14:23
 */
@FunctionalInterface
public interface Function4<T,U,V,W,R> extends MultipleArgumentsFunction<R> {
    /**
     * support four parameters
     *
     * @param t param 1
     * @param u param 2
     * @param v param 3
     * @param w param 4
     * @return return value
     */
    R apply(T t, U u, V v ,W w);
}
