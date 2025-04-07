package com.atguigu.spzx.product.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.product.Category;

import java.util.List;

public interface CategoryService {
    List<Category> selectOneCategory();

    List<Category> findCategoryTree();

}
