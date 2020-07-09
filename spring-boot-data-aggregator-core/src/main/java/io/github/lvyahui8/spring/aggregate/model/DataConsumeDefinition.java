package io.github.lvyahui8.spring.aggregate.model;


import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 22:13
 */
public class DataConsumeDefinition {
    private String             id;
    private Class<?>           clazz;
    private Boolean            ignoreException;
    private Map<String,String> dynamicParameterKeyMap;
    private String             originalParameterName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Boolean getIgnoreException() {
        return ignoreException;
    }

    public void setIgnoreException(Boolean ignoreException) {
        this.ignoreException = ignoreException;
    }

    public Map<String, String> getDynamicParameterKeyMap() {
        return dynamicParameterKeyMap;
    }

    public void setDynamicParameterKeyMap(Map<String, String> dynamicParameterKeyMap) {
        this.dynamicParameterKeyMap = dynamicParameterKeyMap;
    }

    public String getOriginalParameterName() {
        return originalParameterName;
    }

    public void setOriginalParameterName(String originalParameterName) {
        this.originalParameterName = originalParameterName;
    }
}
