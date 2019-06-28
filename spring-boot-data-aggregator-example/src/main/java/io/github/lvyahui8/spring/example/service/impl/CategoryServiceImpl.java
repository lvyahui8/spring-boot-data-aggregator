package io.github.lvyahui8.spring.example.service.impl;

import io.github.lvyahui8.spring.annotation.DataConsumer;
import io.github.lvyahui8.spring.annotation.DataProvider;
import io.github.lvyahui8.spring.annotation.InvokeParameter;
import io.github.lvyahui8.spring.example.model.Category;
import io.github.lvyahui8.spring.example.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/28 23:38
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @DataProvider("rootCategory")
    @Override
    public Category getRootCategory(@DataConsumer("topCategoryNames") List<String> topCategoryNames,@InvokeParameter("categoryId") Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("youtube");
        category.setTopCategoryNames(topCategoryNames);
        return category;
    }

    @DataProvider("categoryTitle")
    @Override
    public String getCategoryTitle(@DataConsumer("rootCategory") Category category,
                                   @DataConsumer("topCategoryNames") List<String> topCategoryNames) {
        if(category.getTopCategoryNames() == topCategoryNames){
            log.info("'user.getFollowers()' and 'followers' may be the same object " +
                    "because the query cache is used. ");
        }
        return category.getName();
    }

    @DataProvider("topCategoryNames")
    public List<String> getTopCategoryNames() {
        Random r = new Random(System.currentTimeMillis());
        return Stream.of("feego", "figo", "sam").map(item -> item+r.nextInt(1000))
                .collect(Collectors.toList());
    }
}
