package io.github.lvyahui8.spring.aggregate.model;


import java.lang.reflect.Parameter;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/3 22:39
 */
public class MethodArg {
    private String     annotationKey;
    private DependType dependType;
    private Parameter  parameter;

    public String getAnnotationKey() {
        return annotationKey;
    }

    public void setAnnotationKey(String annotationKey) {
        this.annotationKey = annotationKey;
    }

    public DependType getDependType() {
        return dependType;
    }

    public void setDependType(DependType dependType) {
        this.dependType = dependType;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
