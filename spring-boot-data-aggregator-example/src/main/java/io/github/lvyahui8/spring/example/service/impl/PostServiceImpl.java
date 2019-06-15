package io.github.lvyahui8.spring.example.service.impl;

import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:54
 */
@Service
public class PostServiceImpl implements PostService {
    @DataProvider("posts")
    @Override
    public List<Post> getPosts(@InvokeParameter("userId") Long userId) {
        Assert.isTrue(userId != null && userId != 0, "userId must be not null and gt 0!");
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
