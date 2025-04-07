package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.mapper.BrandMapper;
import com.atguigu.spzx.manager.service.BrandService;
import com.atguigu.spzx.model.entity.product.Brand;
import com.atguigu.spzx.model.vo.product.BrandVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public BrandVo findByPage(int page, int limit) {
        Page<Brand> brandPage = new Page<>(page, limit);
        brandMapper.selectPage(brandPage, null);
        BrandVo brandVo = new BrandVo();
        brandVo.setBrandList(brandPage.getRecords());
        brandVo.setPageTotal(brandPage.getTotal());
        return brandVo;
    }

    @Override
    public void save(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void updateById(Brand brand) {
        brand.setUpdateTime(new Date());
        brandMapper.updateById(brand);
    }

    @Override
    public void deleteById(Long id) {
        brandMapper.deleteById(id);
    }

    @Override
    public List<Brand> findAll() {
        List<Brand> brandList = brandMapper.selectList(null);
        return brandList;
    }
}
