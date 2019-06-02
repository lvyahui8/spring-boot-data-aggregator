package org.feego.spring.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.feego.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import org.feego.spring.aggregate.facade.impl.DataBeanAggregateQueryFacadeImpl;
import org.feego.spring.aggregate.model.DataProvider;
import org.feego.spring.aggregate.repository.DataProviderRepository;
import org.feego.spring.aggregate.repository.impl.DataProviderRepositoryImpl;
import org.feego.spring.aggregate.service.DataBeanAgregateQueryServiceImpl;
import org.feego.spring.annotation.DataBeanProvider;
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
                new CustomizableThreadFactory("aggregateThread-"));
    }

    private void scanProviders(DataProviderRepository repository) {
        if(properties.getBasePackpages() != null) {
            for (String basePackage : properties.getBasePackpages()) {
                Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
                Set<Method> providerMethods = reflections.getMethodsAnnotatedWith(DataBeanProvider.class);
                for (Method method : providerMethods) {
                    DataProvider provider = new DataProvider();
                    DataBeanProvider beanProvider = method.getAnnotation(DataBeanProvider.class);
                    provider.setId(beanProvider.id());
                    provider.setMethod(method);
                    provider.setTimeout(beanProvider.timeout());
                    repository.put(beanProvider.id(),provider);
                }
            }
        }
    }
}
