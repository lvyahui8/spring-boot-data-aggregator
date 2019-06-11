# Spring Boot Data Bean 并发聚合支持

## 背景与目的

在开发后台接口时, 为了开发效率, 我们往往习惯编写串行执行的代码, 去调用不同的接口, 即使这些接口之间并无依赖,  这使得最后开发的接口性能低下, 且数据不方便复用

**此框架目的旨在保持开发效率的同时, 很方便地支持并发和数据复用**

当然, 在极端高并发的场景下,  并行调用接口对性能提升并不明显,  但不代表这个项目没有价值.  因为互联网世界的大部分应用, 并不会有非常高的并发访问量

## 原理

1. CountDownLatch + Future + 递归
2. 为了得到目标数据, 会递归分析并获取数据做需要的依赖项, **数据的依赖项有两种: 其他接口返回值或者输入参数, 前一种需要调用其他接口, 这个调用将封装为任务异步执行并获取结果**

## 使用方法

pom.xml

```xml
<dependency>
  <groupId>io.github.lvyahui8</groupId>
  <artifactId>spring-boot-data-aggregator-starter</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```

application.properties

```
# 指定要扫描注解的包
io.github.lvyahui8.spring.base-packages=io.github.lvyahui8.spring.example
```

- `@DataProvider` 定义数据提供者
- `@DataConsumer` 定义方法参数依赖类型为其他接口返回值, 其他接口是一个`@DataProvider`
- `@InvokeParameter` 定义方法参数依赖类型为用户输入值
- Spring Bean `DataBeanAggregateQueryFacade` 查询指定的数据的门面

## 示例

开发一个用户汇总数据接口, 包括用户的基础信息和博客列表

### 1. 定义提供基础数据的"原子"服务

使用`@DataProvider`定义接口为数据提供者

使用`@InvokeParameter`指定要传递的用户输入参数

**博客列表服务**

 需要参数`userId`

```java
@Service
public class PostServiceImpl implements PostService {
    @DataProvider(id = "posts")
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

**用户基础信息查询服务**

需要参数`userId`

```java
@Service
public class UserServiceImpl implements UserService {

    @DataProvider(id = "user")
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

### 2. 定义并实现聚合层

组合`@DataProvider` \ `@DataConsumer` \ `@InvokeParameter` 实现汇聚功能

```java
@Component
public class UserAggregate {
    @DataProvider(id="userWithPosts")
    public User userWithPosts(
            @DataConsumer(id = "user") User user,
            @DataConsumer(id = "posts") List<Post> posts) {
        user.setPosts(posts);
        return user;
    }
}
```

### 3. 调用聚合层接口

注解了`@DataProvider`方法的接口不需要直接调用,  而是通过门面类`DataBeanAggregateQueryFacade`访问.

指定要查询的data id, 查询参数, 返回值类型, 并调用`facade.get`方法即可

```java
DataBeanAggregateQueryFacade queryFacade = context.getBean(DataBeanAggregateQueryFacade.class);
User user = queryFacade.get(/*data id*/ "userWithPosts", 
                            /*Invoke Parameters*/
                            Collections.singletonMap("userId",1L), 
                            User.class);
Assert.notNull(user,"user not null");
Assert.notNull(user.getPosts(),"user posts not null");
```

**运行结果**

可以看到, user 和posts是由异步线程执行查询, 而userWithPosts是主调线程执行,  其中 

- 基础user信息查询耗费时间 1000ms
- 用户博客列表查询耗费时间 1000ms
- **总的查询时间 1005ms**

```
[aggregateTask-1]  query id: user, costTime: 1000ms, resultType: User,  invokeMethod: UserServiceImpl#get
[aggregateTask-2]  query id: posts, costTime: 1000ms, resultType: List,  invokeMethod: PostServiceImpl#getPosts
[           main]  query id: userWithPosts, costTime: 1010ms, resultType: User,  invokeMethod: UserAggregate#userWithPosts
[           main]  user.name:lvyahui8,user.posts.size:1
```

## 贡献者

- Feego(lvyauhi8@gmail.com)
- Iris G