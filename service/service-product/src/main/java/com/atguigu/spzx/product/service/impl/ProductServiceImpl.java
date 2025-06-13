package com.atguigu.spzx.product.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.dto.product.SkuSaleDto;
import com.atguigu.spzx.model.entity.product.Product;
import com.atguigu.spzx.model.entity.product.ProductDetails;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.h5.ProductItemVo;
import com.atguigu.spzx.model.vo.h5.ProductSkuVo;
import com.atguigu.spzx.product.mapper.ProductDetailsMapper;
import com.atguigu.spzx.product.mapper.ProductMapper;
import com.atguigu.spzx.product.mapper.ProductSkuMapper;
import com.atguigu.spzx.product.service.ProductService;
import com.atguigu.spzx.product.properties.MinioProperties;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;
    @Autowired
    private MinioProperties minioProperties;

    @Override
    public List<ProductSku> selectProductSkuBySal() {
        List<ProductSku> productSkus = productSkuMapper.selectProductSkuBySal();
        String prefix = minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/";
        for(ProductSku productSku : productSkus){
            productSku.setThumbImg(prefix + productSku.getThumbImg());
        }
        return productSkus;
    }

    @Override
    public ProductSkuVo findByPage(int page, int limit, ProductSkuDto productSkuDto) {
        IPage<ProductSku> productSkuPage = new Page<>(page, limit);
        ProductSkuVo productSkuVo = new ProductSkuVo();
        List<ProductSku> productSkus = productSkuMapper.selectByPage(productSkuPage, productSkuDto);
        String prefix = minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/";
        for(ProductSku productSku : productSkus){
            productSku.setThumbImg(prefix + productSku.getThumbImg());
        }
        productSkuVo.setList(productSkus);
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
        String prefix = minioProperties.getEndpointUrl() + "/" + minioProperties.getBucketName() + "/";
        List<String> sliderUrlList = Arrays.stream(product.getSliderUrls().split(","))
                .map(String::trim) // 去除空格
                .map(link -> prefix + link) // 添加前缀
                .collect(Collectors.toList());// 收集为 List
        ProductDetails productDetails = productDetailsMapper.selectOne(new QueryWrapper<ProductDetails>().eq("product_id", productId));
        List<String> detailsImageUrlList = Arrays.stream(productDetails.getImageUrls().split(","))
                .map(String::trim) // 去除空格
                .map(link -> prefix + link) // 添加前缀
                .collect(Collectors.toList());// 收集为 List
        //5 封装map集合 == 商品规格对应商品skuId信息
        Map<String,Object> skuSpecValueMap = new HashMap<>();
        //根据商品id获取商品所有sku列表
        List<ProductSku> productSkus = productSkuMapper.selectList(new QueryWrapper<ProductSku>().eq("product_id", productId));
        for (ProductSku productSku1 : productSkus) {
            skuSpecValueMap.put(productSku1.getSkuSpec(),productSku1.getId());
        }
        //6 把需要数据封装到ProductItemVo里面
        productItemVo.setProductSku(productSku);
        productItemVo.setProduct(product);
        productItemVo.setSliderUrlList(sliderUrlList);
        productItemVo.setDetailsImageUrlList(detailsImageUrlList);
        productItemVo.setSkuSpecValueMap(skuSpecValueMap);
        productItemVo.setSpecValueList(JSON.parseArray(product.getSpecValue()));
        return productItemVo;
    }

    @Override
    public ProductSku getBySkuId(Long skuId) {
        return productSkuMapper.selectById(skuId);
    }

    @Transactional
    @Override
    public Boolean updateSkuSaleNum(List<SkuSaleDto> skuSaleDtoList) {
        if(!CollectionUtils.isEmpty(skuSaleDtoList)) {
            for(SkuSaleDto skuSaleDto : skuSaleDtoList) {
                LambdaUpdateWrapper<ProductSku> productSkuLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                productSkuLambdaUpdateWrapper.eq(ProductSku::getId, skuSaleDto.getSkuId())
                        .setSql("sale_num = sale_num + " + skuSaleDto.getNum())
                        .setSql("stock_num = stock_num - " + skuSaleDto.getNum());
                productSkuMapper.update(null, productSkuLambdaUpdateWrapper);
            }
        }
        return true;
    }
}
