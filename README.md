# Spring Boot 并行数据聚合库

[![Build Status](https://travis-ci.org/lvyahui8/spring-boot-data-aggregator.svg?branch=develop)](https://travis-ci.org/lvyahui8/spring-boot-data-aggregator)
[![Codecov](https://codecov.io/gh/lvyahui8/spring-boot-data-aggregator/branch/develop/graph/badge.svg)](https://codecov.io/gh/lvyahui8/spring-boot-data-aggregator/branch/develop)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.lvyahui8/spring-boot-data-aggregator-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.lvyahui8/spring-boot-data-aggregator-starter)
[![GitHub release](https://img.shields.io/github/release/lvyahui8/spring-boot-data-aggregator.svg)](https://github.com/lvyahui8/spring-boot-data-aggregator/releases)

## 背景与目的

在开发后台接口时, 为了开发效率, 我们往往习惯编写串行执行的代码, 去调用不同的接口, 即使这些接口之间并无依赖,  这使得最后开发的接口性能低下, 且数据不方便复用

**此框架目的旨在保持开发效率的同时, 很方便地支持并行和数据复用**

当然, 在极端高并发的场景下,CPU很可能已经跑满, 并行调用接口对性能提升并不明显, 但不代表这个项目没有价值. 因为互联网世界的大部分应用, 并不会有非常高的并发访问量

## 特性

- **异步获取依赖**

  所有 `@DataConsumer` 定义的依赖将异步获取. 当provider方法参数中的所有依赖获取完成, 才执行provider方法

- **不限级嵌套**

  依赖关系支持深层嵌套. 下面的示例只有一层

- **异常处理**

  目前支持两种处理方式: 忽略or终止

  忽略是指provider方法在执行时, 忽略抛出的异常并return null值; 终止是指一旦有一个provider方法抛出了异常, 将逐级向上抛出, 终止后续处理.

  配置支持consumer级或者全局, 优先级 : consumer级 > 全局

- **查询缓存**

  在调用Facade的query方法的一次查询生命周期内, **方法调用结果可能复用, 只要方法签名以及传参一致, 则默认方法是幂等的, 将直接使用缓存的查询结果.**   但这个不是绝对的, 考虑到多线程的特性, 可能有时候不会使用缓存

- **超时控制** 

  `@DataProvider` 注解支持配置timeout, 超时将抛出中断异常 (InterruptedException),  遵循异常处理逻辑

## 使用方法

### 1. 配置

pom.xml

```xml
<dependency>
  <groupId>io.github.lvyahui8</groupId>
  <artifactId>spring-boot-data-aggregator-starter</artifactId>
  <version>{$LATEST_VERSION}</version>
</dependency>
```

application.properties

```properties
# 指定要扫描注解的包
io.github.lvyahui8.spring.base-packages=io.github.lvyahui8.spring.example
```

### 2. 添加注解

- `@DataProvider` 定义数据提供者
- `@DataConsumer` 定义方法参数依赖类型为其他接口返回值, 其他接口是一个`@DataProvider`
- `@InvokeParameter` 定义方法参数依赖类型为用户输入值

### 3. 查询

Spring Bean `DataBeanAggregateQueryFacade` 查询指定的数据的门面

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
    @DataProvider("posts")
    @Override
    public List<Post> getPosts(@InvokeParameter("userId") Long userId) {
```

**用户基础信息查询服务**

需要参数`userId`

```java
@Service
public class UserServiceImpl implements UserService {
    @DataProvider("user")
    @Override
    public User get(@InvokeParameter("userId") Long id) {
```

### 2. 调用聚合接口

```java
@Autowired
DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade;
```

#### 方式一: 函数式调用

注意这里不能将函数式调用改为Lambda表达式, 两者的实际行为是不一致的.

```java
User user = dataBeanAggregateQueryFacade.get(
     Collections.singletonMap("userId", 1L), 
     new Function2<User, List<Post>, User>() {
            @Override
            public User apply(@DataConsumer("user") User user, 
                              @DataConsumer("posts") List<Post> posts) {
                user.setPosts(posts);
                return user;
            }
     });
Assert.notNull(user,"user not null");
Assert.notNull(user.getPosts(),"user posts not null");
```

#### 方式二: 定义聚合层查询

组合`@DataProvider` \ `@DataConsumer` \ `@InvokeParameter` 实现汇聚功能

```java
@Component
public class UserAggregate {
    @DataProvider("userWithPosts")
    public User userWithPosts(
            @DataConsumer("user") User user,
            @DataConsumer("posts") List<Post> posts) {
        user.setPosts(posts);
        return user;
    }
}
```

指定要查询的data id, 查询参数, 返回值类型, 并调用`facade.get`方法即可

```java
User user = dataBeanAggregateQueryFacade.get(/*data id*/ "userWithPosts", 
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
