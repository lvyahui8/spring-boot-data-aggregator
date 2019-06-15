package io.github.lvyahui8.spring.example;

import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.example.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/16 0:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBeanAggregateQueryFacadeTest {

    @Autowired
    private DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade;

    @Test
    public void testExceptionProcessing() throws Exception {
        User user = dataBeanAggregateQueryFacade.get("userWithPosts",
                Collections.singletonMap("userId", 0L), User.class);
        System.out.println(user);
    }
}
