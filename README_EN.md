# Spring Boot Data Parallel Aggregation Library

[![Build Status](https://travis-ci.org/lvyahui8/spring-boot-data-aggregator.svg?branch=develop)](https://travis-ci.org/lvyahui8/spring-boot-data-aggregator)
[![Codecov](https://codecov.io/gh/lvyahui8/spring-boot-data-aggregator/branch/develop/graph/badge.svg)](https://codecov.io/gh/lvyahui8/spring-boot-data-aggregator/branch/develop)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.lvyahui8/spring-boot-data-aggregator-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.lvyahui8/spring-boot-data-aggregator-starter)
[![GitHub release](https://img.shields.io/github/release/lvyahui8/spring-boot-data-aggregator.svg)](https://github.com/lvyahui8/spring-boot-data-aggregator/releases)

[![Total alerts](https://img.shields.io/lgtm/alerts/g/lvyahui8/spring-boot-data-aggregator.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/lvyahui8/spring-boot-data-aggregator/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/lvyahui8/spring-boot-data-aggregator.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/lvyahui8/spring-boot-data-aggregator/context:java)

## Background and purpose

When developing the background interface, in order to improve the  development efficiency, we often write serial execution codes to call different interfaces, even if there is no dependency among these interfaces, which causes the last developed interface performance is low, and the data is not convenient to reuse.

**This framework is designed to support parallel and data reuse while maintaining the development efficiency.**

Of course, in an extremely high concurrent scenario, the parallel call interface is not that helpful for performance improvement.   However, it doesn't mean that this project is meaningless  because most applications in the Internet don't have very high concurrent traffic.

## Features

- **Getting dependencies asynchronously**

  All dependencies defined by `@DataConsumer` will be got asynchronously. The provider method is executed when all the dependencies of the provider method parameters are got .

- **Unlimited nesting**

  Dependencies support deep nesting. The follow example has only one layer nesting relationship.

- **Exception handling**

  Currently supports two processing methods: ignore or stop 

  Ignore means that the provider method ignores the exception and returns a null value when it is executed. Stop means that once a provider method throws an exception, it will be thrown up step by step, and stop subsequent processing.

  Exception handling configuration item supports consumer level or global level, and comsumer level is priority to global level

- **Query Cache**

  In one query life cycle of calling the Facade's query method, the result called by DataProvider method may be reused. As long as the method signature and the parameters are consistent, the default method is idempotent, and the cached query result will be used directly.** However, this Not an absolute.  Considering the multi-threading feature, sometimes the cache is not used.

- **Timeout Control**

  `@DataProvider` annotation supports configuration timeout, query timeout will throw interrupt exception (InterruptedException), follow exception handling logic.


## Getting Started

### 1. Configuration

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
# Specify the package to scan the annotations
io.github.lvyahui8.spring.base-packages=io.github.lvyahui8.spring.example
```

### 2. Annotation

- `@DataProvider`:  define the data provider

- `@DataConsumer`: define the method parameter dependency type as return the value of other interfaces, the other interface is a `@DataProvider`

- `@InvokeParameter`: define the method parameter dependency type as the user input value

### 3. Query

Spring Bean `dataBeanAggregateQueryFacade` query data facade API

## Example

Developing a user summary data interface that includes the user's basic information and blog list.

### 1. Define an "atomic" service to provide user data

Use `@DataProvider` to define the interface a data provider.

Use `@InvokeParameter` to specify the input parameters to pass.

**Blog list service**

require input parameter `userId`.

```java
@Service
public class PostServiceImpl implements PostService {
    @DataProvider("posts")
    @Override
    public List<Post> getPosts(@InvokeParameter("userId") Long userId) {
```

**User basic information query service**

require input parameter `userId`.

```java
@Service
public class UserServiceImpl implements UserService {
    @DataProvider("user")
    @Override
    public User get(@InvokeParameter("userId") Long id) {
```

### 2. Call the aggregation interface

```java
@Autowired
DataBeanAggregateQueryFacade dataBeanAggregateQueryFacade;
```

#### Method 1: Functional call

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

####  Method 2: Define and implement an aggregation layer

Combine `@DataProvider`  ( `@DataConsumer`  \ `@InvokeParameter` ) to achieve aggregation function

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

Specify queried data id, invoke parameters, and return type to invoke `facade.get` method

```java
DataBeanAggregateQueryFacade queryFacade = context.getBean(DataBeanAggregateQueryFacade.class);
User user = queryFacade.get(/*data id*/ "userWithPosts",
                            /*Invoke Parameters*/
                            Collections.singletonMap("userId",1L),
                            User.class);
Assert.notNull(user,"user not null");
Assert.notNull(user.getPosts(),"user posts not null");
```

**Invoke result**

As you can see, `user` interface  and `posts` interface are executed by the asynchronous thread while `userWithPosts` service is executed by the calling thread.

- Basic user information query takes 1000ms
- User blog list query takes 1000ms
- **Total query takes 1005ms**

```
[aggregateTask-1] query id: user, costTime: 1000ms, resultType: User, invokeMethod: UserServiceImpl#get
[aggregateTask-2] query id: posts, costTime: 1000ms, resultType: List, invokeMethod: PostServiceImpl#getPosts
[           main] query id: userWithPosts, costTime: 1010ms, resultType: User, invokeMethod: UserAggregate#userWithPosts
[           main] user.name:lvyahui8,user.posts.size:1
```

## Contributors

- Feego (lvyauhi8@gmail.com)

- Iris G
