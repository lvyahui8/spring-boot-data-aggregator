package org.feego.spring.aggregate.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:30
 */
@Data
public class DataProvider {
    private String id;
    private Method method;
    private Long timeout;
    private List<DataDepend> depends;
    private List<InvokeParam> params;
}
