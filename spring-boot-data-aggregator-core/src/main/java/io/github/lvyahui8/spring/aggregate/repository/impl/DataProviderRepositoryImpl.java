package io.github.lvyahui8.spring.aggregate.repository.impl;

import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:27
 */
public class DataProviderRepositoryImpl implements DataProviderRepository {

    private final ConcurrentHashMap<String,DataProvideDefinition> providerMap = new ConcurrentHashMap<>();

    @Override
    public void put(DataProvideDefinition dataProvideDefinition) {
        Assert.notNull(dataProvideDefinition.getId(),"data provider id must be not null!");
        String id = dataProvideDefinition.getId();
        Assert.isTrue(! providerMap.containsKey(id),"data provider exist! id: " + id);
        providerMap.put(id, dataProvideDefinition);
    }

    @Override
    public DataProvideDefinition get(String id) {
        return providerMap.get(id);
    }

    @Override
    public boolean contains(String id) {
        return providerMap.containsKey(id);
    }
}
