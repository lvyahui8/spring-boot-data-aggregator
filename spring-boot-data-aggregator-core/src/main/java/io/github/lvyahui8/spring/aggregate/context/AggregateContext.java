package io.github.lvyahui8.spring.aggregate.context;

import lombok.Data;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/21 22:37
 */
@Data
public class AggregateContext {
    /**
     * 发起一次递归查询的主调线程
     */
    Thread rootThread;
}
