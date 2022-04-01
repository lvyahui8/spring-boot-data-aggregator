package io.github.lvyahui8.spring.aggregate.service;

import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/2
 */
public interface ProviderService {
    /**
     * 通过MultipleArgumentsFunction获取provider实例
     *
     * @param function 多参函数
     * @return provider实例
     * @throws IllegalAccessException ignored
     */
    DataProvideDefinition getProvider(MultipleArgumentsFunction<?> function) throws IllegalAccessException;
}
