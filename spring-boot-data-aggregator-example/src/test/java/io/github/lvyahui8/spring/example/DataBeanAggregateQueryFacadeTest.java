package io.github.lvyahui8.spring.example;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.func.Function2;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.autoconfigure.BeanAggregateProperties;
import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/16 0:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DataBeanAggregateQueryFacadeTest {

    @Autowired
    private DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade;

    @Autowired
    private BeanAggregateProperties beanAggregateProperties;

    @Test
    public void testExceptionProcessing() throws Exception {
        boolean success = false;
        if(! beanAggregateProperties.isIgnoreException()) {
            try {
                dataBeanAggregateQueryFacade.get("userWithPosts",
                        Collections.singletonMap("userId", 1L), User.class);
            } catch (Exception e) {
                log.info("default settings is SUSPEND, catch an exception: {}",e.getMessage(),e);
                success = true;
            }
        } else {
            User user = dataBeanAggregateQueryFacade.get("userWithPosts",
                    Collections.singletonMap("userId", 1L), User.class);
            System.out.println(user);
            success = true;
        }
        Assert.isTrue(success,"exception handle success");
    }

    @Test
    public void testGetByMultipleArgumentsFunction() throws Exception {
        Map<String, Object> singletonMap = Collections.singletonMap("userId", 1L);
        User user = dataBeanAggregateQueryFacade.get(singletonMap, new Function2<User, List<Post>, User>() {
            @Override
            public User apply(@DataConsumer("user") User user, @DataConsumer("posts") List<Post> posts) {
                user.setPosts(posts);
                return user;
            }
        },null);
        log.info("query result:{} ",user);
        try {
            user = dataBeanAggregateQueryFacade.get(singletonMap, (Function2<User, List<Post>, User>) (user1, posts) -> {
                user1.setPosts(posts);
                return user1;
            },null);
        } catch (Exception e) {
            log.info("don't support lambda!!! eMsg:{}",e.getMessage());
        }
    }
}
