# Spring Boot Data Bean 并发聚合支持

## 背景

后台接口在调用一些接口时, 为了写代码方便, 多数时候我们习惯串行调用, 即使这些接口调用并无依赖性.

此框架目的旨在保持开发的简便性, 并同时支持并发和数据复用

## 原理

1. CountDownLatch + Future
2. 目标参数依赖中, 需要查询接口获取的数据, 封装为一个任务交给线程池处理, 处理完成之后, 再统一目标方法

## 使用方法

- DataBeanProvider 定义数据model提供者
- DataBeanConsumer 定义方法将要消费的model
- InvokeParameter 指定用户手动传入的参数
- DataBeanAggregateQueryFacade 查询指定的model

## 示例

开发一个用户汇总数据接口, 包括用户的用户基础信息和博客列表

**1. 定义提供基础数据的"原子"服务**

使用DataBeanProvider定义要提供的数据, 使用InvokeParameter指定查询时要传递的参数

博客列表服务, 需要参数userId

```java
@Service
public class PostServiceImpl implements PostService {
    @DataBeanProvider(id = "posts")
    @Override
    public List<Post> getPosts(@InvokeParameter("userId") Long userId) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            //
        }
        Post post = new Post();
        post.setTitle("spring data aggregate example");
        post.setContent("No active profile set, falling back to default profiles");
        return Collections.singletonList(post);
    }
}

```

用户基础信息查询服务, 需要参数userId

```java
@Service
public class UserServiceImpl implements UserService {

    @DataBeanProvider(id = "user")
    @Override
    public User get(@InvokeParameter("userId") Long id) {
        /* */
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            //
        }
        /* mock a user*/
        User user = new User();
        user.setId(id);
        user.setEmail("lvyahui8@gmail.com");
        user.setUsername("lvyahui8");
        return user;
    }
}
```

**2. 定义并实现聚合层**

组合DataBeanProvider\DataBeanConsumer\InvokeParameter实现汇聚功能

```java
@Component
public class UserAggregate {
    @DataBeanProvider(id="userWithPosts")
    public User userWithPosts(
            @DataBeanConsumer(id = "user") User user,
            @DataBeanConsumer(id = "posts") List<Post> posts) {
        user.setPosts(posts);
        return user;
    }
}
```

**3. 调用聚合层接口**

注解了DataBeanProvider方法的接口都不能直接调用, 而需要通过门面类DataBeanAggregateQueryFacade访问.

指定要查询的DataBeanProvider.id, 查询参数, 返回值类型即可

```java
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
```

**运行结果**

可以看到, user 和posts是由异步线程进行的查询, 而userWithPosts是主调线程

其中 

- 基础user信息查询时间 1000ms
- 用户博客列表查询时间 1000ms
- **总的查询时间 1005ms**

```
2019-06-03 23:56:52.254  INFO 9088 --- [aggregateTask-2] f.s.a.s.DataBeanAgregateQueryServiceImpl : query id: posts, costTime: 1000ms, model: List, params: 1
2019-06-03 23:56:52.254  INFO 9088 --- [aggregateTask-1] f.s.a.s.DataBeanAgregateQueryServiceImpl : query id: user, costTime: 1000ms, model: User, params: 1
2019-06-03 23:56:52.255  INFO 9088 --- [           main] f.s.a.s.DataBeanAgregateQueryServiceImpl : query id: userWithPosts, costTime: 1005ms, model: User, params: User...........
```

## 作者

Feego(lvyauhi8@gmail.com)