package io.github.lvyahui8.spring.example;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.github.lvyahui8.spring.aggregate.facade.DataBeanAggregateQueryFacade;
import io.github.lvyahui8.spring.aggregate.func.Function2;
import io.github.lvyahui8.spring.aggregate.func.Function3;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DynamicParameter;
import io.github.lvyahui8.spring.autoconfigure.BeanAggregateProperties;
import io.github.lvyahui8.spring.example.context.ExampleAppContext;
import io.github.lvyahui8.spring.example.context.RequestContext;
import io.github.lvyahui8.spring.example.facade.UserQueryFacade;
import io.github.lvyahui8.spring.example.model.Category;
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

    private static final int NUM = 100;

    @Autowired
    private DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade;

    @Autowired
    private BeanAggregateProperties beanAggregateProperties;

    @Autowired
    UserQueryFacade userQueryFacade;

    @Test
    public void testSample() throws Exception {
        {
            User user = dataBeanAggregateQueryFacade.get("userWithPosts", Collections.singletonMap("userId",1L), User.class);
            Assert.notNull(user,"user not null");
            Assert.notNull(user.getPosts(),"user posts not null");
            log.info("user.name:{},user.posts.size:{}",
                    user.getUsername(),user.getPosts().size());
        }

        log.info("------------------------------------------------------------------");

        {
            User user = userQueryFacade.getUserFinal(1L);
            Assert.notNull(user.getFollowers(),"user followers not null");
        }

        log.info("------------------------------------------------------------------");

        {
            for (int i = 0; i < NUM; i ++) {
                String s = dataBeanAggregateQueryFacade.get("categoryTitle", Collections.singletonMap("categoryId", 1L), String.class);
                Assert.isTrue(org.apache.commons.lang3.StringUtils.isNotEmpty(s),"s  not null");
            }
        }
    }

    @Test
    public void testExceptionProcessing() throws Exception {
        boolean success = false;
        if(! beanAggregateProperties.isIgnoreException()) {
            try {
                dataBeanAggregateQueryFacade.get("userWithPosts",
                        Collections.singletonMap("userId", 1L), User.class);
            } catch (Exception e) {
                log.info("default settings is SUSPEND, catch an exception: {}",e.getMessage(),e);
            }
        } else {
            User user = dataBeanAggregateQueryFacade.get("userWithPosts",
                    Collections.singletonMap("userId", 1L), User.class);
            Assert.notNull(user,"user must be not null!");
        }
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
        Assert.notNull(user,"user never not be null!");
        try {
            user = dataBeanAggregateQueryFacade.get(singletonMap, (Function2<User, List<Post>, User>) (user1, posts) -> {
                user1.setPosts(posts);
                return user1;
            },null);
        } catch (Exception e) {
            log.info("don't support lambda!!! eMsg:{}",e.getMessage());
        }
    }

    @Test
    public void testInheritableThreadLocals() throws Exception {
        try {
            User user = new User();
            user.setUsername("bob");
            user.setId(100000L);
            ExampleAppContext.setLoggedUser(user);
            dataBeanAggregateQueryFacade.get(null, new Function2<String,List<User>,User>() {
                @Override
                public User apply(@DataConsumer("loggedUsername") String loggedUsername,
                                  @DataConsumer("loggedUserFollowers") List<User> loggedUserFollowers) {
                    Assert.notNull(loggedUsername, "loggedUsername must be not null");
                    Assert.notNull(loggedUserFollowers, "loggedUserFollowers must be not null");
                    Assert.notNull(ExampleAppContext.getUsername(),"ExampleAppContext.getUsername() must be not null");
                    log.info("everything is normal~");
                    return null;
                }
            });
        } finally {
            ExampleAppContext.remove();
        }
    }

    @Test
    public void testThreadLocal() throws Exception {
        try {
            RequestContext.setTenantId(10000L);
            Object result = dataBeanAggregateQueryFacade.get(null,
                    new Function3<List<Category>, List<Post>, List<User>, Object>() {
                @Override
                public Object apply(
                        @DataConsumer("topMenu") List<Category> categories,
                        @DataConsumer("postList") List<Post> posts,
                        @DataConsumer("allFollowers") List<User> users) {
                    return new Object[] {
                            categories,posts,users
                    };
                }
            });
        } finally {
            RequestContext.removeTenantId();
        }
    }

    @Test
    public void testDynamicParameter() throws Exception {
        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("userA_Id", 1L)
                .put("userB_Id", 2L)
                .put("userC_Id", 3L).build();
        Object specialUserCollection = dataBeanAggregateQueryFacade.get(params,
                new Function3<User,User,User,Object>() {
                    @Override
                    public Object apply(
                            @DataConsumer(id = "user",
                                    dynamicParameters = {@DynamicParameter(targetKey = "userId" ,replacementKey = "userA_Id")}) User userA,
                            @DataConsumer(id = "user",
                                    dynamicParameters = {@DynamicParameter(targetKey = "userId" ,replacementKey = "userB_Id")}) User userB,
                            @DataConsumer(id = "user",
                                    dynamicParameters = {@DynamicParameter(targetKey = "userId" ,replacementKey = "userC_Id")}) User userC) {
                        return Lists.newArrayList(userA,userB,userC);
                    }
                });
        System.out.println(specialUserCollection instanceof List);
        System.out.println(specialUserCollection);
    }

    @Test
    public void testParameterTypeException() throws Exception {
        try{
            dataBeanAggregateQueryFacade.get(Collections.singletonMap("userId", "1"),
                    new Function2<User, List<Post>, User>() {
                        @Override
                        public User apply(@DataConsumer("user") User user,
                                          @DataConsumer("posts") List<Post> posts) {
                            return user;
                        }
                    });
            throw new IllegalStateException("must throw IllegalArgumentException");
        } catch (Exception e) {
            log.error("eMsg:",e);
            Assert.isTrue(e instanceof IllegalArgumentException,"e must be typeof IllegalArgumentException");
        }
    }

    @Test
    public void testAnonymousProviderCache() throws Exception {
        Function2<Category, List<String>, String> userFunction = new Function2<Category, List<String>, String>() {
            @Override
            public String apply(@DataConsumer("rootCategory") Category category,
                              @DataConsumer("topCategoryNames") List<String> topCategoryNames) {
                return category.getName();
            }
        };
        Map<String, Object> map = Collections.singletonMap("userId", 1L);
        Exception exp = null;
        for (int i = 0; i < 1000; i ++) {
            try {
                String name =  dataBeanAggregateQueryFacade.get(map,userFunction);
            } catch (Exception e) {
                exp = e;
                log.error("exp:" + e.getMessage());
            }
        }
        Assert.isNull(exp,"exp must be null!");
    }
}
