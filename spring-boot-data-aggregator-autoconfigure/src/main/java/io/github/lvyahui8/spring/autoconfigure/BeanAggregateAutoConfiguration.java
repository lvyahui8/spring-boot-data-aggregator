package io.github.lvyahui8.spring.autoconfigure;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.facade.impl.DataBeanAggregateQueryFacadeImpl;
import io.github.lvyahui8.spring.aggregate.model.*;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.repository.impl.DataProviderRepositoryImpl;
import io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAgregateQueryServiceImpl;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
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
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.lang.reflect.Method;
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
        DataBeanAgregateQueryServiceImpl service = new DataBeanAgregateQueryServiceImpl(repository);
        service.setExecutorService(aggregateExecutorService());
        service.setApplicationContext(applicationContext);
        return new DataBeanAggregateQueryFacadeImpl(service);
    }

    @Bean(name = "aggregateExecutorService")
    @ConditionalOnMissingBean(name = "aggregateExecutorService")
    public ExecutorService aggregateExecutorService() {
        log.info("create a ThreadPoolExecutor");
        return new ThreadPoolExecutor(
                4,
                properties.getThreadNumber()  < 4 ? 4 : properties.getThreadNumber() ,
                2L, TimeUnit.HOURS,
                new LinkedBlockingDeque<>(properties.getQueueSize()),
                new CustomizableThreadFactory("aggregateTask-"));
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
        DataProvideDefination provider = new DataProvideDefination();
        DataProvider beanProvider = method.getAnnotation(DataProvider.class);
        provider.setId(beanProvider.id());
        provider.setMethod(method);
        provider.setTimeout(beanProvider.timeout());
        Parameter[] parameters = provider.getMethod().getParameters();
        List<MethodArg> methodArgs = new ArrayList<>(method.getParameterCount());
        provider.setDepends(new ArrayList<>(method.getParameterCount()));
        provider.setParams(new ArrayList<>(method.getParameterCount()));
        for (Parameter parameter : parameters) {
            dealMethodParamter(provider, methodArgs, parameter);
        }
        provider.setMethodArgs(methodArgs);
        repository.put(beanProvider.id(),provider);
    }

    private void dealMethodParamter(DataProvideDefination provideDefination, List<MethodArg> methodArgs, Parameter parameter) {
        MethodArg methodArg = new MethodArg();
        DataConsumer bean = parameter.getAnnotation(DataConsumer.class);
        InvokeParameter invokeParameter = parameter.getAnnotation(InvokeParameter.class);
        if(bean != null) {
            methodArg.setAnnotionKey(bean.id());
            methodArg.setDenpendType(DenpendType.OTHER_MODEL);
            DataConsumeDefination dataConsumeDefination = new DataConsumeDefination();
            dataConsumeDefination.setClazz(parameter.getType());
            dataConsumeDefination.setId(bean.id());
            provideDefination.getDepends().add(dataConsumeDefination);
        } else if (invokeParameter != null){
            methodArg.setAnnotionKey(invokeParameter.value());
            methodArg.setDenpendType(DenpendType.INVOKE_PARAM);
            InvokeParameterDefination parameterDefination = new InvokeParameterDefination();
            parameterDefination.setKey(invokeParameter.value());
            provideDefination.getParams().add(parameterDefination);
        } else {
            throw new IllegalArgumentException(
                    "paramter must ananotion by InvokeParameter or DataConsumer");
        }
        methodArg.setParameter(parameter);
        methodArgs.add(methodArg);
    }
}
