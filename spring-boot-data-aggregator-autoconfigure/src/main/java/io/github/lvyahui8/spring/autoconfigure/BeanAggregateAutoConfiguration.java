package io.github.lvyahui8.spring.autoconfigure;

import io.github.lvyahui8.spring.aggregate.config.RuntimeSettings;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.facade.impl.DataBeanAggregateQueryFacadeImpl;
import io.github.lvyahui8.spring.aggregate.model.*;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.repository.impl.DataProviderRepositoryImpl;
import io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAggregateQueryServiceImpl;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import io.github.lvyahui8.spring.enums.ExceptionProcessingMethod;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/5/31 23:28
 */
@Configuration
@EnableConfigurationProperties(BeanAggregateProperties.class)
@Slf4j
public class BeanAggregateAutoConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private BeanAggregateProperties properties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade() {
        DataProviderRepository repository = new DataProviderRepositoryImpl();
        scanProviders(repository);
        DataBeanAggregateQueryServiceImpl service = new DataBeanAggregateQueryServiceImpl(repository);
        service.setRuntimeSettings(createRuntimeSettings());
        service.setExecutorService(aggregateExecutorService());
        service.setApplicationContext(applicationContext);
        return new DataBeanAggregateQueryFacadeImpl(service);
    }

    @Bean(name = "aggregateExecutorService")
    @ConditionalOnMissingBean(name = "aggregateExecutorService")
    public ExecutorService aggregateExecutorService() {
        return new ThreadPoolExecutor(
                properties.getThreadNumber(),
                properties.getThreadNumber() ,
                2L, TimeUnit.HOURS,
                new LinkedBlockingDeque<>(properties.getQueueSize()),
                new CustomizableThreadFactory(properties.getThreadPrefix()));
    }

    private void scanProviders(DataProviderRepository repository) {
        if(properties.getBasePackages() != null) {
            for (String basePackage : properties.getBasePackages()) {
                Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
                Set<Method> providerMethods = reflections.getMethodsAnnotatedWith(DataProvider.class);
                for (Method method : providerMethods) {
                    dealProvideMethod(repository, method);
                }
            }
        }
    }

    private void dealProvideMethod(DataProviderRepository repository, Method method) {
        DataProvideDefinition provider = new DataProvideDefinition();
        DataProvider beanProvider = AnnotationUtils.findAnnotation(method, DataProvider.class);
        @SuppressWarnings("ConstantConditions")
        String dataId = beanProvider.id();
        Assert.isTrue(Modifier.isPublic(method.getModifiers()),"data provider method must be public");
        Assert.isTrue(! StringUtils.isEmpty(dataId),"data id must be not null!");
        provider.setId(dataId);
        provider.setMethod(method);
        provider.setTimeout(beanProvider.timeout() > 0 ? beanProvider.timeout() : properties.getDefaultTimeout());
        Parameter[] parameters = provider.getMethod().getParameters();
        List<MethodArg> methodArgs = new ArrayList<>(method.getParameterCount());
        provider.setDepends(new ArrayList<>(method.getParameterCount()));
        provider.setParams(new ArrayList<>(method.getParameterCount()));
        for (Parameter parameter : parameters) {
            dealMethodParameter(provider, methodArgs, parameter);
        }
        provider.setMethodArgs(methodArgs);
        repository.put(dataId,provider);
    }

    private void dealMethodParameter(DataProvideDefinition provideDefinition, List<MethodArg> methodArgs, Parameter parameter) {
        DataConsumer dataConsumer = AnnotationUtils.findAnnotation(parameter, DataConsumer.class);
        InvokeParameter invokeParameter = AnnotationUtils.findAnnotation(parameter,InvokeParameter.class);
        Assert.isTrue(dataConsumer != null || invokeParameter != null,
                "Parameters must be added @InvokeParameter or @DataConsumer annotation");
        MethodArg methodArg = new MethodArg();
        if(dataConsumer != null) {
            String dataId = dataConsumer.id();
            Assert.isTrue(! StringUtils.isEmpty(dataId),"data id must be not null!");
            methodArg.setAnnotationKey(dataId);
            methodArg.setDependType(DependType.OTHER_MODEL);
            DataConsumeDefinition dataConsumeDefinition = new DataConsumeDefinition();
            dataConsumeDefinition.setClazz(parameter.getType());
            dataConsumeDefinition.setId(dataId);
            if(! dataConsumer.exceptionProcessingMethod().equals(ExceptionProcessingMethod.BY_DEFAULT)) {
                dataConsumeDefinition.setIgnoreException(
                        dataConsumer.exceptionProcessingMethod().equals(ExceptionProcessingMethod.IGNORE)
                );
            }
            provideDefinition.getDepends().add(dataConsumeDefinition);
        } else {
            methodArg.setAnnotationKey(invokeParameter.value());
            methodArg.setDependType(DependType.INVOKE_PARAM);
            InvokeParameterDefinition parameterDefinition = new InvokeParameterDefinition();
            parameterDefinition.setKey(invokeParameter.value());
            provideDefinition.getParams().add(parameterDefinition);
        }
        methodArg.setParameter(parameter);
        methodArgs.add(methodArg);
    }

    private RuntimeSettings createRuntimeSettings() {
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setEnableLogging(properties.getEnableLogging() != null
                ? properties.getEnableLogging() : false);
        runtimeSettings.setIgnoreException(properties.isIgnoreException());
        return runtimeSettings;
    }

}
