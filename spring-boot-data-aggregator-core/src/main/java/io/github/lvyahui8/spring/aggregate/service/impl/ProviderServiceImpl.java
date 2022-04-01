package io.github.lvyahui8.spring.aggregate.service.impl;

import io.github.lvyahui8.spring.aggregate.func.MultipleArgumentsFunction;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.service.ProviderService;
import io.github.lvyahui8.spring.aggregate.util.DefinitionUtils;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author feego lvyahui8@gmail.com
 * @date 2022/4/2
 */
public class ProviderServiceImpl implements ProviderService {
    @Setter
    DataProviderRepository repository;
    @Override
    public DataProvideDefinition getProvider(MultipleArgumentsFunction<?> multipleArgumentsFunction) throws IllegalAccessException {
        DataProvideDefinition provider = repository.get(multipleArgumentsFunction.getClass().getName());
        if(provider != null) {
            return provider;
        }
        Method[] methods = multipleArgumentsFunction.getClass().getMethods();
        Method applyMethod = null;


        for (Method method : methods) {
            if(! Modifier.isStatic(method.getModifiers()) && ! method.isDefault()) {
                applyMethod = method;
                break;
            }
        }

        if(applyMethod == null) {
            throw new IllegalAccessException(multipleArgumentsFunction.getClass().getName());
        }

        provider = DefinitionUtils.getProvideDefinition(applyMethod);
        provider.setTarget(multipleArgumentsFunction);
        provider.setId(multipleArgumentsFunction.getClass().getName());
        repository.put(provider);
        return provider;
    }

}
