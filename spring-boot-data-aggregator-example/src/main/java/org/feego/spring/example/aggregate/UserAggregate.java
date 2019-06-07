package org.feego.spring.example.aggregate;

import org.feego.spring.annotation.DataConsumer;
import org.feego.spring.annotation.DataProvider;
import org.feego.spring.example.model.Post;
import org.feego.spring.example.model.User;
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
