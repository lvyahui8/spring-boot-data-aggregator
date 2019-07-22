package io.github.lvyahui8.spring.example.service.impl;

import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.example.context.RequestContext;
import io.github.lvyahui8.spring.example.model.Category;
import io.github.lvyahui8.spring.example.model.Post;
import io.github.lvyahui8.spring.example.model.User;
import io.github.lvyahui8.spring.example.service.HomepageService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/22 23:08
 */
@Service
public class HomepageServiceImpl implements HomepageService {

    @DataProvider("topMenu")
    @Override
    public List<Category> topMenu() {
        /* will be null */
        Long tenantId = RequestContext.getTenantId();
        Assert.notNull(tenantId,"tenantId must be not null");
        // ... The content hereafter will be omitted.
        return null;
    }

    @DataProvider("postList")
    @Override
    public List<Post> postList() {
        /* will be null */
        Long tenantId = RequestContext.getTenantId();
        Assert.notNull(tenantId,"tenantId must be not null");
        // ... The content hereafter will be omitted.
        return null;
    }

    @DataProvider("allFollowers")
    @Override
    public List<User> allFollowers() {
        /* will be null */
        Long tenantId = RequestContext.getTenantId();
        Assert.notNull(tenantId,"tenantId must be not null");
        // ... The content hereafter will be omitted.
        return null;
    }
}
