package com.atguigu.spzx.product.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.entity.product.Product;
import com.atguigu.spzx.model.entity.product.ProductDetails;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.h5.ProductItemVo;
import com.atguigu.spzx.model.vo.h5.ProductSkuVo;
import com.atguigu.spzx.product.mapper.ProductDetailsMapper;
import com.atguigu.spzx.product.mapper.ProductMapper;
import com.atguigu.spzx.product.mapper.ProductSkuMapper;
import com.atguigu.spzx.product.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;

    @Override
    public List<ProductSku> selectProductSkuBySal() {
        List<ProductSku> productSkus = productSkuMapper.selectProductSkuBySal();
        return productSkus;
    }

    @Override
    public ProductSkuVo findByPage(int page, int limit, ProductSkuDto productSkuDto) {
        IPage<ProductSku> productSkuPage = new Page<>(page, limit);
        ProductSkuVo productSkuVo = new ProductSkuVo();
        productSkuVo.setList(productSkuMapper.selectByPage(productSkuPage, productSkuDto));
        productSkuVo.setTotal(productSkuPage.getTotal());
        return productSkuVo;
    }

    @Override
    public ProductItemVo item(Long skuId) {
        //1 创建vo对象，用于封装最终数据
        ProductItemVo productItemVo = new ProductItemVo();
        //2 根据skuId获取sku信息
        ProductSku productSku = productSkuMapper.selectById(skuId);
        //3 从sku获取productId，获取商品信息
        Long productId = productSku.getProductId();
        //4 从productId获取商品详情信息
        Product product = productMapper.selectById(productId);
        String[] sliderUrlsSplit = product.getSliderUrls().split(",");
        ProductDetails productDetails = productDetailsMapper.selectOne(new QueryWrapper<ProductDetails>().eq("product_id", productId));
        String[] split = productDetails.getImageUrls().split(",");
        //5 封装map集合 == 商品规格对应商品skuId信息
        Map<String,Object> skuSpecValueMap = new HashMap<String,Object>();
        //根据商品id获取商品所有sku列表
        List<ProductSku> productSkus = productSkuMapper.selectList(new QueryWrapper<ProductSku>().eq("product_id", productId));
        for (ProductSku productSku1 : productSkus) {
            skuSpecValueMap.put(productSku1.getSkuSpec(),productSku1.getId());
        }
        //6 把需要数据封装到ProductItemVo里面
        productItemVo.setProductSku(productSku);
        productItemVo.setProduct(product);
        productItemVo.setSliderUrlList(Arrays.asList(sliderUrlsSplit));
        productItemVo.setDetailsImageUrlList(Arrays.asList(split));
        productItemVo.setSkuSpecValueMap(skuSpecValueMap);
        productItemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));
        return productItemVo;
    }
}
