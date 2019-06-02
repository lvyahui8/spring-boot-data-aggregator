package org.feego.spring.aggregate.facade.impl;

import org.feego.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import org.feego.spring.aggregate.service.DataBeanAgregateQueryService;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:22
 */
public class DataBeanAggregateQueryFacadeImpl implements DataBeanAggregateQueryFacade {

    private DataBeanAgregateQueryService dataBeanAgregateQueryService;

    public DataBeanAggregateQueryFacadeImpl(DataBeanAgregateQueryService dataBeanAgregateQueryService) {
        this.dataBeanAgregateQueryService = dataBeanAgregateQueryService;
    }

    @Override
    public <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz) {
        Assert.notNull(id,"id must be not null!");
        Assert.notNull(clazz,"clazz must be not null !");
        if(invokeParams == null) {
            invokeParams = Collections.emptyMap();
        }
        return dataBeanAgregateQueryService.get(id,invokeParams,clazz);
    }
}
