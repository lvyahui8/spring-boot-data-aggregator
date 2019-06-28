package io.github.lvyahui8.spring.example.model;

import lombok.Data;

import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/6/28 23:34
 */
@Data
public class Category {
    private Long id;
    private String name;
    List<String> topCategoryNames;
}
