package org.feego.spring.example;

import org.feego.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import org.feego.spring.example.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/1 0:13
 */

@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleApplication.class);
        DataBeanAggregateQueryFacade queryFacade = context.getBean(DataBeanAggregateQueryFacade.class);
        User user = queryFacade.get("userWithPosts", Collections.singletonMap("userId",1L), User.class);
        Assert.notNull(user,"user not null");
        Assert.notNull(user.getPosts(),"user posts not null");
        System.out.println(user);
    }
}
