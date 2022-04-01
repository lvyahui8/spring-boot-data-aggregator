package io.github.lvyahui8.spring.aggregate2;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateService;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/2
 */
public class AggregateServiceV2 implements DataBeanAggregateService {
    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> resultType) throws InterruptedException, InvocationTargetException, IllegalAccessException {
        final Context context = new Context();
        context.paramMap = invokeParams;
        return null;
    }

    @Override
    public <T> T get(DataProvideDefinition provider, Map<String, Object> invokeParams, Class<T> resultType) throws InterruptedException, InvocationTargetException, IllegalAccessException {
        return null;
    }
}
