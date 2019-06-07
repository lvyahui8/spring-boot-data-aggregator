package org.feego.spring.aggregate.model;

import lombok.Data;

import java.lang.reflect.Parameter;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/3 22:39
 */
@Data
public class MethodArg {
    private String annotionKey;
    private DenpendType denpendType;
    private Parameter parameter;
}
