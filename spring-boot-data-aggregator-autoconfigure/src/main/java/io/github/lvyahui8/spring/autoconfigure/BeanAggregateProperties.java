package io.github.lvyahui8.spring.autoconfigure;

import io.github.lvyahui8.spring.aggregate.service.AsyncQueryTaskWrapper;
import io.github.lvyahui8.spring.aggregate.service.AsyncQueryTaskWrapperAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/5/31 23:50
 */
@ConfigurationProperties(prefix = "spring.data.aggregator")
public class BeanAggregateProperties {
    /**
     * Packages that need to scan for aggregated annotations
     */
    private String[]                               basePackages;
    /**
     * Thread name prefix for asynchronous threads
     */
    private String                          threadPrefix    = "aggregateTask-";
    /**
     * Thread size of the asynchronous thread pool
     */
    private int      threadNumber    = Runtime.getRuntime().availableProcessors() * 3;
    /**
     * The size of the queue that holds the task to be executed
     */
    private int                                    queueSize       = 1000;
    /**
     * Set a default timeout for the method of providing data
     */
    private Long                                   defaultTimeout   = 3000L;
    /**
     * Ignore exception thrown by asynchronous execution, method returns null value
     */
    private boolean                                ignoreException  = false;
    /**
     * Async task implement
     */
    private Class<? extends AsyncQueryTaskWrapper> taskWrapperClass = AsyncQueryTaskWrapperAdapter.class;

    public String[] getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
    }

    public String getThreadPrefix() {
        return threadPrefix;
    }

    public void setThreadPrefix(String threadPrefix) {
        this.threadPrefix = threadPrefix;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public Long getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(Long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public boolean isIgnoreException() {
        return ignoreException;
    }

    public void setIgnoreException(boolean ignoreException) {
        this.ignoreException = ignoreException;
    }

    public Class<? extends AsyncQueryTaskWrapper> getTaskWrapperClass() {
        return taskWrapperClass;
    }

    public void setTaskWrapperClass(Class<? extends AsyncQueryTaskWrapper> taskWrapperClass) {
        this.taskWrapperClass = taskWrapperClass;
    }
}
