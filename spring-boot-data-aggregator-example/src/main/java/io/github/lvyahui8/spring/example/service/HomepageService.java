package io.github.lvyahui8.spring.example.service;

import io.github.lvyahui8.spring.example.model.Category;
import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.model.User;

import java.util.List;

/**
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/22 22:25
 */
public interface HomepageService {
    /**
     * 获取顶部菜单类目
     *
     * @return 顶部类目
     */
    List<Category> topMenu();

    /**
     * 文章列表
     *
     * @return xx
     */
    List<Post> postList();

    /**
     * 粉丝数据
     * @return xxx
     */
    List<User> allFollowers();
}
