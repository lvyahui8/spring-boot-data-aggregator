package io.github.lvyahui8.spring.aggregate.model;


import java.lang.reflect.Method;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:30
 */
public class DataProvideDefinition {
    private String                          id;
    private Method                          method;
    private Object                          target;
    private Long                            timeout;
    private List<DataConsumeDefinition>     depends;
    private List<InvokeParameterDefinition> params;
    private List<MethodArg>                 methodArgs;
    private boolean                         idempotent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public List<DataConsumeDefinition> getDepends() {
        return depends;
    }

    public void setDepends(List<DataConsumeDefinition> depends) {
        this.depends = depends;
    }

    public List<InvokeParameterDefinition> getParams() {
        return params;
    }

    public void setParams(List<InvokeParameterDefinition> params) {
        this.params = params;
    }

    public List<MethodArg> getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(List<MethodArg> methodArgs) {
        this.methodArgs = methodArgs;
    }

    public boolean isIdempotent() {
        return idempotent;
    }

    public void setIdempotent(boolean idempotent) {
        this.idempotent = idempotent;
    }
}
