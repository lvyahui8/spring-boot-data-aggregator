package io.github.lvyahui8.spring.example.facade;

import io.github.lvyahui8.spring.aggregate.facade.DataAggregateQueryFacade;
import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.model.User;
import io.github.lvyahui8.spring.example.service.FollowService;
import io.github.lvyahui8.spring.example.service.PostService;
import io.github.lvyahui8.spring.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/11 21:46
 */
@Component
public class UserQueryFacade {
    @Autowired
    private FollowService followService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @Autowired
    private DataAggregateQueryFacade dataAggregateQueryFacade;

    public User getUserData(Long userId) {
        User user = userService.get(userId);
        user.setPosts(postService.getPosts(userId));
        user.setFollowers(followService.getFollowers(userId));
        return user;
    }

    public User getUserDataByParallel(Long userId) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        Future<User> userFuture = executorService.submit(() -> {
            try{
                return userService.get(userId);
            }finally {
                countDownLatch.countDown();
            }
        });
        Future<List<Post>> postsFuture = executorService.submit(() -> {
            try{
                return postService.getPosts(userId);
            }finally {
                countDownLatch.countDown();
            }
        });
        Future<List<User>> followersFuture = executorService.submit(() -> {
            try{
                return followService.getFollowers(userId);
            }finally {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        User user = userFuture.get();
        user.setFollowers(followersFuture.get());
        user.setPosts(postsFuture.get());
        return user;
    }

    public User getUserFinal(Long userId) throws InterruptedException,
            IllegalAccessException, InvocationTargetException {
        return dataAggregateQueryFacade.get("userFullData",
                Collections.singletonMap("userId", userId), User.class);
    }
}
