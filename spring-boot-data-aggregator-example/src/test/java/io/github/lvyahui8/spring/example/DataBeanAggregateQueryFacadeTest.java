package io.github.lvyahui8.spring.example;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.autoconfigure.BeanAggregateProperties;
import io.github.lvyahui8.spring.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.Collections;

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
                        Collections.singletonMap("userId", 0L), User.class);
            } catch (Exception e) {
                log.info("default settings is SUSPEND, catch an exception: {}",e.getMessage(),e);
                success = true;
            }
        } else {
            User user = dataBeanAggregateQueryFacade.get("userWithPosts",
                    Collections.singletonMap("userId", 0L), User.class);
            System.out.println(user);
            success = true;
        }
        Assert.isTrue(success,"exception handle success");
    }
}
