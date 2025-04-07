package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.mapper.ProductSpecMapper;
import com.atguigu.spzx.manager.service.ProductSpecService;
import com.atguigu.spzx.model.entity.product.ProductSpec;
import com.atguigu.spzx.model.vo.product.ProductSpecVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductSpecServiceImpl implements ProductSpecService {

    @Autowired
    private ProductSpecMapper productSpecMapper;

    @Override
    public ProductSpecVo findByPage(int page, int limit) {
        Page<ProductSpec> productSpecPage = new Page<>(page, limit);
        productSpecMapper.selectPage(productSpecPage, null);
        ProductSpecVo productSpecVo = new ProductSpecVo();
        productSpecVo.setProductSpecs(productSpecPage.getRecords());
        productSpecVo.setPageTotal(productSpecPage.getTotal());
        return productSpecVo;
    }

    @Override
    public void save(ProductSpec productSpec) {
        productSpecMapper.insert(productSpec);
    }

    @Override
    public void updateById(ProductSpec productSpec) {
        productSpec.setUpdateTime(new Date());
        productSpecMapper.updateById(productSpec);
    }

    @Override
    public void deleteById(int id) {
        productSpecMapper.deleteById(id);
    }

    @Override
    public List<ProductSpec> findAll() {
        List<ProductSpec> productSpecs = productSpecMapper.selectList(null);
        return productSpecs;
    }
}
