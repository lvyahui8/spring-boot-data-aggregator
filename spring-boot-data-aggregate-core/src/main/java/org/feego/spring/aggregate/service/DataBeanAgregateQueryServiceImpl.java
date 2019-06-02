package org.feego.spring.aggregate.service;

import lombok.Setter;
import org.feego.spring.aggregate.model.DataDepend;
import org.feego.spring.aggregate.model.DataProvider;
import org.feego.spring.aggregate.repository.DataProviderRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 21:50
 */
public class DataBeanAgregateQueryServiceImpl implements DataBeanAgregateQueryService {

    private DataProviderRepository repository;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private ExecutorService executorService;

    public DataBeanAgregateQueryServiceImpl(DataProviderRepository repository) {
        this.repository = repository;
    }

    /**
     * 并发+递归获取并拼装数据
     *
     * @param id
     * @param invokeParams
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> clazz) {
        Assert.isTrue(repository.contains(id),"id not exisit");
        DataProvider provider = repository.get(id);

        if(provider.getDepends() != null) {
            for (DataDepend depend : provider.getDepends()) {
                get(depend.getId(),invokeParams,depend.getClazz());
            }
        }

        return null;
    }

}
