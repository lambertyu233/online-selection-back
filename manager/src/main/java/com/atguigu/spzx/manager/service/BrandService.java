package com.atguigu.spzx.manager.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.product.Brand;
import com.atguigu.spzx.model.vo.product.BrandVo;

import java.util.List;

public interface BrandService {
    BrandVo findByPage(int page, int limit);

    void save(Brand brand);

    void updateById(Brand brand);

    void deleteById(Long id);

    List<Brand> findAll();

}
