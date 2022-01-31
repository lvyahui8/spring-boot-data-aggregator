package io.github.lvyahui8.spring.aggregate.model;

import lombok.Data;

import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 22:13
 */
@Data
public class DataConsumeDefinition {
    private String             id;
    /**
     * consumer定义在那个接口类中
     */
    private Class<?>           clazz;
    /**
     * 是否忽略consumer调用产生的异常
     */
    private Boolean            ignoreException;
    private Map<String,String> dynamicParameterKeyMap;
    private String             originalParameterName;
}
