package io.github.lvyahui8.spring.example.service.impl;

import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import io.github.lvyahui8.spring.example.model.User;
import io.github.lvyahui8.spring.example.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:36
 */
@Service
public class UserServiceImpl implements UserService {

    @DataProvider("user")
    @Override
    public User get(@InvokeParameter("userId") Long id) {
        /* */
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            //
        }
        /* mock a user*/
        User user = new User();
        user.setId(id);
        user.setEmail("lvyahui8@gmail.com");
        user.setUsername("lvyahui8");
        return user;
    }
}
