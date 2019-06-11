package io.github.lvyahui8.spring.example.service.impl;

import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import io.github.lvyahui8.spring.example.model.User;
import io.github.lvyahui8.spring.example.service.FollowService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/11 21:32
 */
@Service
public class FollowServiceImpl implements FollowService {
    @DataProvider(id = "followers")
    @Override
    public List<User> getFollowers(@InvokeParameter("userId") Long userId) {
        try { Thread.sleep(1000L); } catch (InterruptedException e) {}
        int size = 10;
        List<User> users = new ArrayList<>(size);
        for(int i = 0 ; i < size; i++) {
            User user = new User();
            user.setUsername("name"+i);
            user.setEmail("email"+i+"@fox.com");
            user.setId((long) i);
            users.add(user);
        };
        return users;
    }
}
