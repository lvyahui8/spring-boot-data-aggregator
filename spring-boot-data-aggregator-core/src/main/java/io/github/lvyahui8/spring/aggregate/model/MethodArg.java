package io.github.lvyahui8.spring.aggregate.model;

import lombok.Data;

import java.lang.reflect.Parameter;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/3 22:39
 */
@Data
public class MethodArg {
    private String     annotationKey;
    private DependType dependType;
    private Parameter  parameter;
}
