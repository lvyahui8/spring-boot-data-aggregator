package io.github.lvyahui8.spring.example;

import lombok.extern.slf4j.Slf4j;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.example.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.concurrent.ExecutorService;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:13
 */

@SpringBootApplication
@Slf4j
public class ExampleApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleApplication.class);
        DataBeanAggregateQueryFacade queryFacade = context.getBean(DataBeanAggregateQueryFacade.class);

        User user = queryFacade.get("userWithPosts", Collections.singletonMap("userId",1L), User.class);
        Assert.notNull(user,"user not null");
        Assert.notNull(user.getPosts(),"user posts not null");
        log.info("user.name:{},user.posts.size:{}",
                user.getUsername(),user.getPosts().size());

        ExecutorService executorService = (ExecutorService) context.getBean("aggregateExecutorService");
        executorService.shutdown();
    }
}
