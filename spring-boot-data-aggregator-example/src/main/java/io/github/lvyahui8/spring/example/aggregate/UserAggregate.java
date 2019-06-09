package io.github.lvyahui8.spring.example.aggregate;

import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:42
 */
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
