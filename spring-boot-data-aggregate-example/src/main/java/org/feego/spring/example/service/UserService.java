package org.feego.spring.example.service;

import org.feego.spring.example.model.User;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:36
 */
public interface UserService {
    User get(Long id);
}
