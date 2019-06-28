package io.github.lvyahui8.spring.example.service;

import io.github.lvyahui8.spring.example.model.Category;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/28 23:34
 */
public interface CategoryService {
    Category getRootCategory(List<String> topCategoryNames,Long id);

    String getCategoryTitle(Category category, List<String> topCategoryNames);

    List<String> getTopCategoryNames();
}
