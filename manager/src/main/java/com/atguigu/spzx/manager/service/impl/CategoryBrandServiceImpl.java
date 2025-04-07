package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.mapper.CategoryBrandMapper;
import com.atguigu.spzx.manager.service.CategoryBrandService;
import com.atguigu.spzx.model.dto.product.CategoryBrandDto;
import com.atguigu.spzx.model.entity.product.Brand;
import com.atguigu.spzx.model.entity.product.CategoryBrand;
import com.atguigu.spzx.model.vo.product.CategoryBrandVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CategoryBrandServiceImpl implements CategoryBrandService {

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public CategoryBrandVo findByPage(int page, int limit, CategoryBrandDto categoryBrandDto) {
        Page<CategoryBrand> categoryBrandPage = new Page<>(page, limit);
        IPage<CategoryBrand> categoryBrandIPage = categoryBrandMapper.selectByPage(categoryBrandPage, categoryBrandDto);
        CategoryBrandVo categoryBrandVo = new CategoryBrandVo();
        categoryBrandVo.setCategoryBrands(categoryBrandIPage.getRecords());
        categoryBrandVo.setPageTotal(categoryBrandIPage.getTotal());
        return categoryBrandVo;
    }

    @Override
    public void save(CategoryBrand categoryBrand) {
        categoryBrandMapper.insert(categoryBrand);
    }

    @Override
    public void updateById(CategoryBrand categoryBrand) {
        categoryBrand.setUpdateTime(new Date());
        categoryBrandMapper.updateById(categoryBrand);
    }

    @Override
    public void deleteById(int id) {
        categoryBrandMapper.deleteById(id);
    }

    @Override
    public List<Brand> findBrandByCategoryId(Long categoryId) {
        List<Brand> brandList = categoryBrandMapper.findBrandByCategoryId(categoryId);
        return brandList;
    }
}
