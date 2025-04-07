package com.atguigu.spzx.manager.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.product.ProductSpec;
import com.atguigu.spzx.model.vo.product.ProductSpecVo;

import java.util.List;

public interface ProductSpecService {
    ProductSpecVo findByPage(int page, int limit);

    void save(ProductSpec productSpec);

    void updateById(ProductSpec productSpec);

    void deleteById(int id);

    List<ProductSpec> findAll();

}
