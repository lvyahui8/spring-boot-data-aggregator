package io.github.lvyahui8.spring.example.service;

import io.github.lvyahui8.spring.example.model.User;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/11 21:31
 */
public interface FollowService {
    List<User> getFollowers(Long userId);
}
