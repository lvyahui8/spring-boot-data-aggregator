package io.github.lvyahui8.spring.aggregate.repository;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:26
 */
public interface DataProviderRepository {
    void put(String id, DataProvideDefinition dataProvideDefinition);

    DataProvideDefinition get(String id);

    boolean contains(String id);
}
