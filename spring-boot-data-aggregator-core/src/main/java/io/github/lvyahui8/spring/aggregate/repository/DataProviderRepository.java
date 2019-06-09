package io.github.lvyahui8.spring.aggregate.repository;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefination;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefination;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:26
 */
public interface DataProviderRepository {
    void put(String id, DataProvideDefination dataProvideDefination);

    DataProvideDefination get(String id);

    boolean contains(String id);
}
