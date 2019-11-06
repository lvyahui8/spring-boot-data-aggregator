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
    private Class<?>           clazz;
    private Boolean            ignoreException;
    private Map<String,String> dynamicParameterKeyMap;
    private String             originalParameterName;
}
