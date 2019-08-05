package io.github.lvyahui8.spring.aggregate.repository;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:26
 */
public interface DataProviderRepository {
    /**
     * 存放provide定义
     * @param dataProvideDefinition provider定义
     */
    void put(DataProvideDefinition dataProvideDefinition);

    /**
     * 获取provider
     *
     * @param id data provider id
     * @return data provider
     */
    DataProvideDefinition get(String id);

    /**
     * 是否包含指定Provider
     * @param id data provider id
     * @return 是否存在provider
     */
    boolean contains(String id);
}
