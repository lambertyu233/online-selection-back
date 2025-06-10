package com.atguigu.spzx.product.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.product.mapper.CategoryMapper;
import com.atguigu.spzx.product.properties.MinioProperties;
import com.atguigu.spzx.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private MinioProperties minioProperties;

//    获取全部一级菜单
//    现用redis缓存进行改造，提升页面的加载速度
    //TODO:redis 缓存添加虽然加快了访问速度，但是如果MySql数据库更新了数据怎么办，会导致redis数据跟mysql数据不一致，给前端返回错误数据
    @Override
    public List<Category> selectOneCategory() {
        //1 查询redis，是否有所有一级分类
        String categoryOneJson = redisTemplate.opsForValue().get("category::one");
        //2 如果redis包含所有一级分类，直接返回
        if(StringUtils.hasText(categoryOneJson)){
            //categoryOneJson字符串转换List<Category>
            return JSON.parseArray(categoryOneJson, Category.class);
        }
        //3 如果redis没有所有一级分类，查询数据库，把查询内容返回，并且把内容放到redis里面
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        List<Category> categorylist = categoryMapper.selectList(queryWrapper);
        String prefix = minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/";
        for(Category category : categorylist){
            category.setImageUrl(prefix + category.getImageUrl());
        }
        redisTemplate.opsForValue().set("category::one",JSON.toJSONString(categorylist),1, TimeUnit.DAYS);
        return categorylist;
    }

//  获取全部菜单以及子菜单
    //用Spring Cache的注解来实现缓存
    @Cacheable(value = "category",key = "'all'")//存在redis里面的key为category::all
    @Override
    public List<Category> findCategoryTree() {
        List<Category> categoryList = categoryMapper.selectList(null);
        String prefix = minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/";
        List<Category> categoryTree = new ArrayList<>();
        for(Category category : categoryList){
            category.setImageUrl(prefix + category.getImageUrl());
            if(category.getParentId() == 0){
                categoryTree.add(buildTree(category, categoryList));
            }
        }
        return categoryTree;
    }

//    构建树形菜单结构的函数
    private Category buildTree(Category categoryFather,List<Category> categoryList) {
        categoryFather.setChildren(new ArrayList<>());
        for (Category category : categoryList) {
            if(category.getParentId().equals(categoryFather.getId())) {
                Category children = buildTree(category,categoryList);
                categoryFather.getChildren().add(children);
            }
        }
        return categoryFather;
    }
}
