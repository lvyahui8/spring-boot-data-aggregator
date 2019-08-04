package io.github.lvyahui8.spring.autoconfigure;

import io.github.lvyahui8.spring.aggregate.config.RuntimeSettings;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.facade.impl.DataBeanAggregateQueryFacadeImpl;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptorChain;
import io.github.lvyahui8.spring.aggregate.interceptor.impl.AggregateQueryInterceptorChainImpl;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.repository.impl.DataProviderRepositoryImpl;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateQueryService;
import io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAggregateQueryServiceImpl;
import io.github.lvyahui8.spring.aggregate.util.DefinitionUtils;
import io.github.lvyahui8.spring.annotation.DataProvider;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade(
            @Qualifier("dataProviderRepository") DataProviderRepository dataProviderRepository) {
        return new DataBeanAggregateQueryFacadeImpl(dataBeanAggregateQueryService(dataProviderRepository));
    }

    @Bean
    @ConditionalOnMissingBean
    public DataBeanAggregateQueryService dataBeanAggregateQueryService (
            @Qualifier("dataProviderRepository") DataProviderRepository dataProviderRepository) {
        if(properties.getBasePackages() != null) {
            for (String basePackage : properties.getBasePackages()) {
                Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
                Set<Method> providerMethods = reflections.getMethodsAnnotatedWith(DataProvider.class);
                for (Method method : providerMethods) {
                    DataProvider beanProvider = AnnotationUtils.findAnnotation(method, DataProvider.class);
                    @SuppressWarnings("ConstantConditions")
                    String dataId = beanProvider.id();
                    Assert.isTrue(Modifier.isPublic(method.getModifiers()),"data provider method must be public");
                    Assert.isTrue(! StringUtils.isEmpty(dataId),"data id must be not null!");
                    DataProvideDefinition provider = DefinitionUtils.getProvideDefinition(method);
                    provider.setId(dataId);
                    provider.setIdempotent(beanProvider.idempotent());
                    provider.setTimeout(beanProvider.timeout() > 0 ? beanProvider.timeout() : properties.getDefaultTimeout());
                    dataProviderRepository.put(provider);
                }
            }
        }
        DataBeanAggregateQueryServiceImpl service = new DataBeanAggregateQueryServiceImpl();
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setEnableLogging(properties.getEnableLogging() != null
                ? properties.getEnableLogging() : false);
        runtimeSettings.setIgnoreException(properties.isIgnoreException());
        runtimeSettings.setTimeout(properties.getDefaultTimeout());
        service.setRepository(dataProviderRepository);
        service.setRuntimeSettings(runtimeSettings);
        service.setExecutorService(aggregateExecutorService());
        service.setInterceptorChain(aggregateQueryInterceptorChain());
        service.setApplicationContext(applicationContext);
        return service;
    }

    /**
     * 允许用户自定义线程池
     *
     * @return
     */
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

    /**
     * 允许用户自定义provider存储
     *
     * @return
     */
    @Bean(name = "dataProviderRepository")
    @ConditionalOnMissingBean(DataProviderRepository.class)
    public DataProviderRepository dataProviderRepository() {
        return new DataProviderRepositoryImpl();
    }


    @Bean(name = "aggregateQueryInterceptorChain")
    @ConditionalOnMissingBean(AggregateQueryInterceptorChain.class)
    public AggregateQueryInterceptorChain aggregateQueryInterceptorChain() {
        return new AggregateQueryInterceptorChainImpl();
    }
}
