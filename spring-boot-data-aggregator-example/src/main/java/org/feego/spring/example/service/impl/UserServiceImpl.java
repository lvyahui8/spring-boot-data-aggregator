package org.feego.spring.example.service.impl;

import org.feego.spring.annotation.DataProvider;
import org.feego.spring.annotation.InvokeParameter;
import org.feego.spring.example.model.User;
import org.feego.spring.example.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:36
 */
@Service
public class UserServiceImpl implements UserService {

    @DataProvider(id = "user")
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
