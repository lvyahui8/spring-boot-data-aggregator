package org.feego.spring.example.service;

import org.feego.spring.example.model.Post;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:53
 */
public interface PostService {
    List<Post> getPosts(Long userId);
}
