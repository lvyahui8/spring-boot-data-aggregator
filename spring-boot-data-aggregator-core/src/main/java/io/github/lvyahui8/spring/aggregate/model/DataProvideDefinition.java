package io.github.lvyahui8.spring.aggregate.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:30
 */
@Data
public class DataProvideDefinition {
    private String                          id;
    private Method                          method;
    private Object                          target;
    private Long                            timeout;
    private List<DataConsumeDefinition>     depends;
    private List<InvokeParameterDefinition> params;
    private List<MethodArg>                 methodArgs;
    private boolean                         idempotent;
}
