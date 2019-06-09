# Spring Boot Data Bean Parallel Aggregation Support

## Background and purpose

When developing the background interface, in order to improve the  development efficiency, we often write serial execution codes to call different interfaces, even if there is no dependency among these interfaces, which causes the last developed interface performance is low, and the data is not convenient to reuse.

**This framework is designed to support parallel and data reuse while maintaining the development efficiency.**

Of course, in an extremely high concurrent scenario, the parallel call interface is not that helpful for performance improvement.   However, it doesn't mean that this project is meaningless  because most applications in the Internet don't have very high concurrent traffic.

## Principle

1. CountDownLatch + Future + Recursion
2. In order to get the target data, it will recursively analyze and obtain the dependencies required by the data.  **There are two kinds of dependencies; one is other interface return value, the other is  input parameter.  The former needs to call other interfaces, this call will be packed as a task which is an asynchronous execution to get the result.**

## Instruction

```xml
<dependency>
  <groupId>io.github.lvyahui8</groupId>
  <artifactId>spring-boot-data-aggregator-starter</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```

- `@DataProvider`:  define the data provider
- `@DataConsumer`: define the method parameter dependency type as return the value of other interfaces, the other interface is a @DataProvider
- `@InvokeParameter`: define the method parameter dependency type as the user input value
- Spring Bean `dataBeanAggregateQueryFacade`:  query data facade API

## Example

Developing a user summary data interface that includes the user's basic information and blog list.

### 1. Define an "atomic" service to provide user data

Use `@DataProvider` to define the interface a data provider.

Use `@InvokeParameter` to specify the input parameters to pass.

**Blog list service**

require input parameter `userId`.

```java
@Service
Public class PostServiceImpl implements PostService {
    @DataProvider(id = "posts")
    @Override
    Public List<Post> getPosts(@InvokeParameter("userId") Long userId) {
        Try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        Post post = new Post();
        post.setTitle("spring data aggregate example");
        post.setContent("No active profile set, falling back to default profiles");
        Return Collections.singletonList(post);
    }
}
```

**User basic information query service**

require input parameter `userId`.

```java
@Service
Public class UserServiceImpl implements UserService {

    @DataProvider(id = "user")
    @Override
    Public User get(@InvokeParameter("userId") Long id) {
        /* */
        Try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
        }
        /* mock a user*/
        User user = new User();
        user.setId(id);
        user.setEmail("lvyahui8@gmail.com");
        user.setUsername("lvyahui8");
        Return user;
    }
}
```

### 2. Define and implement an aggregation layer

Combine `@DataProvider`  ( `@DataConsumer`  \ `@InvokeParameter` ) to achieve aggregation function

```java
@Component
Public class UserAggregate {
    @DataProvider(id="userWithPosts")
    Public User userWithPosts(
            @DataConsumer(id = "user") User user,
            @DataConsumer(id = "posts") List<Post> posts) {
        user.setPosts(posts);
        Return user;
    }
}
```

### 3. Call the aggregation layer interface

Note that the interface of the `@DataProvider` method shouldn't to be called directly, but accessed through the facade class `DataBeanAggregateQueryFacade`.

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