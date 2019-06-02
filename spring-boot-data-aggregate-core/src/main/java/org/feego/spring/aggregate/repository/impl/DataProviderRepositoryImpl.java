package org.feego.spring.aggregate.repository.impl;

import org.feego.spring.aggregate.model.DataProvider;
import org.feego.spring.aggregate.repository.DataProviderRepository;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:27
 */
public class DataProviderRepositoryImpl implements DataProviderRepository {

    private ConcurrentHashMap<String,DataProvider> providerMap = new ConcurrentHashMap<>();

    @Override
    public void put(String id, DataProvider dataProvider) {
        Assert.isTrue(! providerMap.containsKey(id),"data provider exisit! id: " + id);
        providerMap.put(id,dataProvider);
    }

    @Override
    public DataProvider get(String id) {
        return providerMap.get(id);
    }

    @Override
    public boolean contains(String id) {
        return providerMap.containsKey(id);
    }
}
