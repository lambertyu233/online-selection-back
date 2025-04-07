package com.atguigu.spzx.manager.mapper;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.dto.product.CategoryBrandDto;
import com.atguigu.spzx.model.entity.product.Brand;
import com.atguigu.spzx.model.entity.product.CategoryBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface CategoryBrandMapper extends BaseMapper<CategoryBrand> {

    IPage<CategoryBrand> selectByPage(Page<CategoryBrand> page, CategoryBrandDto categoryBrandDto);

    List<Brand> findBrandByCategoryId(Long categoryId);

}
