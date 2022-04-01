package io.github.lvyahui8.spring.aggregate2;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/1
 */
public class ResourceNode {
    Object target;
    Method method;

    Map<String,ResourceNode> dependents;
}
