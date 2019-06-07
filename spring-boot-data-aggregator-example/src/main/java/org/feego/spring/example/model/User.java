package org.feego.spring.example.model;

import lombok.Data;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/2 16:38
 */
@Data
public class User {
    private Long id;
    private String username;
    private String email;
    List<Post> posts ;
}
