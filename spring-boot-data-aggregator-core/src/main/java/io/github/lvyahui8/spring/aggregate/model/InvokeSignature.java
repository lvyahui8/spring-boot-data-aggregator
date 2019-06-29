package io.github.lvyahui8.spring.aggregate.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/28 22:42
 */
@Data
public class InvokeSignature {
    private Method method;
    private Object[] args;

    public InvokeSignature(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokeSignature that = (InvokeSignature) o;
        return Objects.deepEquals(method, that.method) &&
                Arrays.deepEquals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
