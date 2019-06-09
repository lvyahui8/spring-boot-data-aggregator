package io.github.lvyahui8.spring.aggregate.repository.impl;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefination;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:27
 */
public class DataProviderRepositoryImpl implements DataProviderRepository {

    private ConcurrentHashMap<String,DataProvideDefination> providerMap = new ConcurrentHashMap<>();

    @Override
    public void put(String id, DataProvideDefination dataProvideDefination) {
        Assert.isTrue(! providerMap.containsKey(id),"data provider exisit! id: " + id);
        providerMap.put(id, dataProvideDefination);
    }

    @Override
    public DataProvideDefination get(String id) {
        return providerMap.get(id);
    }

    @Override
    public boolean contains(String id) {
        return providerMap.containsKey(id);
    }
}
