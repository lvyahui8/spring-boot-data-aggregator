package org.feego.spring.aggregate.repository;

import org.feego.spring.aggregate.model.DataProvider;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:26
 */
public interface DataProviderRepository {
    void put(String id, DataProvider dataProvider);

    DataProvider get(String id);

    boolean contains(String id);
}
