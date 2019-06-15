package io.github.lvyahui8.spring.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/5/31 23:50
 */
@ConfigurationProperties(prefix = "io.github.lvyahui8.spring")
@Data
public class BeanAggregateProperties {
    /**
     * Packages that need to scan for aggregated annotations
     */
    private String[] basePackages;
    /**
     * Thread name prefix for asynchronous threads
     */
    private String   threadPrefix    = "aggregateTask-";
    /**
     * Thread size of the asynchronous thread pool
     */
    private int      threadNumber    = Runtime.getRuntime().availableProcessors() * 3;
    /**
     * The size of the queue that holds the task to be executed
     */
    private int      queueSize       = 1000;
    /**
     * Set a default timeout for the method of providing data
     */
    private Long     defaultTimeout  = 3000L;
    /**
     * Allow output log
     */
    private Boolean  enableLogging   = true;
    /**
     * Ignore exception thrown by asynchronous execution, method returns null value
     */
    private boolean ignoreException = false;
}
