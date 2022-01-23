package io.github.lvyahui8.spring.aggregate.func;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/13 23:21
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface Function3<T,U,V,R> extends MultipleArgumentsFunction<R> {
    /**
     * support three parameters
     *
     * @param t param 1
     * @param u param 2
     * @param v param 3
     * @return return value
     */
    R apply(T t, U u,V v);
}
