package com.atguigu.spzx.test;


import com.atguigu.spzx.manager.ManagerApplication;
import com.atguigu.spzx.manager.mapper.CategoryMapper;
import com.atguigu.spzx.manager.service.ProductService;
import com.atguigu.spzx.model.dto.product.ProductDto;
import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.product.ProductVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = ManagerApplication.class)
public class AllTest {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductService productService;

    //把商品菜单表没有子菜单的has_children变成1
    @Test
    public void test01(){
        List<Long> noChildrenIds = categoryMapper.getNoChildrenIds();
        for(Long id : noChildrenIds){
            Category category = new Category();
            category.setId(id);
            category.setHasChildren(true);
            categoryMapper.updateById(category);
        }
    }

    @Test
    public void test02(){
        ProductDto productDto = new ProductDto();
        ProductVo productVo = productService.findByPage(1,3,productDto);
        System.out.println(Result.build(productVo, ResultCodeEnum.SUCCESS));
    }
}
