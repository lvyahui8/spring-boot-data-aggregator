package io.github.lvyahui8.spring.example.service;

import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.model.Post;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:53
 */
public interface PostService {
    List<Post> getPosts(Long userId);
}
