package io.github.lvyahui8.spring.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:13
 */

@SpringBootApplication
@Slf4j
public class ExampleApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = null;
        try{
            context =  SpringApplication.run(ExampleApplication.class,args);
        } finally {
            if(context != null) {
                ExecutorService executorService = (ExecutorService) context.getBean("aggregateExecutorService");
                executorService.shutdown();
            }
        }
    }

}
