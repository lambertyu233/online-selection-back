package com.atguigu.spzx.test;


import com.atguigu.spzx.product.ProductApplication;
import com.atguigu.spzx.product.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProductApplication.class)
public class SpringTest {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    public void test() {
        System.out.println(categoryServiceImpl.findCategoryTree());
    }
}
