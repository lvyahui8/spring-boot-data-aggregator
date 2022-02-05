package io.github.lvyahui8.spring.aggregate.context;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.model.InvokeSignature;
import lombok.Data;

import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/21 22:37
 */
@Data
public class AggregationContext {
    /**
     * 发起一次递归查询的主调线程
     */
    Thread                      rootThread;
    /**
     * 根provider
     */
    DataProvideDefinition       rootProvideDefinition;
    /**
     * 此次查询生命周期中的缓存
     */
    Map<InvokeSignature,Object> cacheMap;

    /**
     * 传入的参数
     */
    Map<String,Object> invokeParams;
}
