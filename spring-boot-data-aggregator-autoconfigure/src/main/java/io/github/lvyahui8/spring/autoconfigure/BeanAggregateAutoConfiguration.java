package io.github.lvyahui8.spring.autoconfigure;

import io.github.lvyahui8.spring.aggregate.config.RuntimeSettings;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.facade.impl.DataBeanAggregateQueryFacadeImpl;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptor;
import io.github.lvyahui8.spring.aggregate.interceptor.AggregateQueryInterceptorChain;
import io.github.lvyahui8.spring.aggregate.interceptor.impl.AggregateQueryInterceptorChainImpl;
import io.github.lvyahui8.spring.aggregate.model.DataConsumeDefinition;
import io.github.lvyahui8.spring.aggregate.model.DataProvideDefinition;
import io.github.lvyahui8.spring.aggregate.repository.DataProviderRepository;
import io.github.lvyahui8.spring.aggregate.repository.impl.DataProviderRepositoryImpl;
import io.github.lvyahui8.spring.aggregate.service.DataBeanAggregateService;
import io.github.lvyahui8.spring.aggregate.service.impl.DataBeanAggregateServiceImpl;
import io.github.lvyahui8.spring.aggregate.util.DefinitionUtils;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.facade.FacadeInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        DataBeanAggregateQueryFacadeImpl facade = new DataBeanAggregateQueryFacadeImpl(dataBeanAggregateQueryService(dataProviderRepository));
        FacadeInitializer.initFacade(facade);
        return facade;
    }

    private void checkCycle(Map<String,Set<String>> graphAdjMap) {
        Map<String,Integer> visitStatusMap = new HashMap<>(graphAdjMap.size() * 2);
        for (Map.Entry<String, Set<String>> item : graphAdjMap.entrySet()) {
            if (visitStatusMap.containsKey(item.getKey())) {
                continue;
            }
            dfs(graphAdjMap,visitStatusMap,item.getKey());
        }
    }

    private void dfs(Map<String,Set<String>> graphAdjMap,Map<String,Integer> visitStatusMap, String node) {
        if (visitStatusMap.containsKey(node)) {
            if(visitStatusMap.get(node) == 1) {
                List<String> relatedNodes = new ArrayList<>();
                for (Map.Entry<String,Integer> item : visitStatusMap.entrySet()) {
                    if (item.getValue() == 1) {
                        relatedNodes.add(item.getKey());
                    }
                }
                throw new IllegalStateException("There are loops in the dependency graph. Related nodes:" + StringUtils.join(relatedNodes));
            }
            return ;
        }
        visitStatusMap.put(node,1);
        log.info("visited:{}", node);
        for (String relateNode : graphAdjMap.get(node)) {
            dfs(graphAdjMap,visitStatusMap,relateNode);
        }
        visitStatusMap.put(node,2);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataBeanAggregateService dataBeanAggregateQueryService (
            @Qualifier("dataProviderRepository") DataProviderRepository dataProviderRepository) {
        if(properties.getBasePackages() != null) {
            Map<String,Set<String>> provideDependMap = new HashMap<>(64);
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
                    Assert.isTrue(! dataProviderRepository.contains(dataId), "Data providers with the same name are not allowed. dataId: " + dataId);
                    provideDependMap.put(dataId,provider.getDepends().stream().map(DataConsumeDefinition::getId).collect(Collectors.toSet()));
                    dataProviderRepository.put(provider);
                }
            }
            checkCycle(provideDependMap);
        }

        DataBeanAggregateServiceImpl service = new DataBeanAggregateServiceImpl();
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setIgnoreException(properties.isIgnoreException());
        runtimeSettings.setTimeout(properties.getDefaultTimeout());
        service.setRepository(dataProviderRepository);
        service.setRuntimeSettings(runtimeSettings);
        service.setExecutorService(aggregateExecutorService());
        service.setInterceptorChain(aggregateQueryInterceptorChain());
        service.setTaskWrapperClazz(properties.getTaskWrapperClass());
        service.setApplicationContext(applicationContext);
        return service;
    }

    /**
     * 允许用户自定义线程池
     *
     * @return 线程池服务
     */
    @Bean(name = "aggregateExecutorService")
    @ConditionalOnMissingBean(name = "aggregateExecutorService",value=ExecutorService.class)
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
     * @return  provider数据仓库
     */
    @Bean(name = "dataProviderRepository")
    @ConditionalOnMissingBean(DataProviderRepository.class)
    public DataProviderRepository dataProviderRepository() {
        return new DataProviderRepositoryImpl();
    }


    @Bean(name = "aggregateQueryInterceptorChain")
    @ConditionalOnMissingBean(AggregateQueryInterceptorChain.class)
    public AggregateQueryInterceptorChain aggregateQueryInterceptorChain() {
        Map<String, AggregateQueryInterceptor> interceptorMap = applicationContext.getBeansOfType(AggregateQueryInterceptor.class);
        AggregateQueryInterceptorChainImpl interceptorChain = new AggregateQueryInterceptorChainImpl();
        if(! interceptorMap.isEmpty()) {
            List<AggregateQueryInterceptor> interceptors = new ArrayList<>(interceptorMap.values());
            interceptors.sort((o1, o2) -> {
                Order order1 = o1.getClass().getAnnotation(Order.class);
                Order order2 = o2.getClass().getAnnotation(Order.class);
                int oi1 = order1 == null ? Ordered.LOWEST_PRECEDENCE :  order1.value();
                int oi2 = order2 == null ?  Ordered.LOWEST_PRECEDENCE : order2.value();
                return oi1 - oi2;
            });
            for (AggregateQueryInterceptor interceptor : interceptors) {
                interceptorChain.addInterceptor(interceptor);
            }
        }
        return interceptorChain;
    }
}
