package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.mapper.ProductDetailsMapper;
import com.atguigu.spzx.manager.mapper.ProductMapper;
import com.atguigu.spzx.manager.mapper.ProductSkuMapper;
import com.atguigu.spzx.manager.service.ProductService;
import com.atguigu.spzx.model.dto.product.ProductDto;
import com.atguigu.spzx.model.entity.product.Product;
import com.atguigu.spzx.model.entity.product.ProductDetails;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.product.ProductVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;
    @Autowired
    private FileUploadServiceImpl fileUploadService;

    //对产品名称，品牌名称，一级二级三级分类的分页查询
    @Override
    public ProductVo findByPage(int page, int limit, ProductDto productDto) {
        Page<Product> productPage = new Page<>(page, limit);
        productMapper.findByPage(productPage,productDto);
        ProductVo productVo = new ProductVo();
        productVo.setList(productPage.getRecords());
        productVo.setTotal(productPage.getTotal());
        return productVo;
    }

    //添加产品的所有详细信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Product product) {
        productMapper.insert(product);
        String detailsImageUrls = product.getDetailsImageUrls();
        try {
            if (detailsImageUrls != null && !detailsImageUrls.isEmpty()) {
                ProductDetails productDetails = new ProductDetails();
                productDetails.setProductId(product.getId());
                productDetails.setImageUrls(detailsImageUrls);
                productDetailsMapper.insert(productDetails);
            }
        }catch (Exception e){
            fileUploadService.deleteFile(detailsImageUrls);
            throw e;
        }
        List<ProductSku> productSkuList = product.getProductSkuList();
        if (productSkuList != null && !productSkuList.isEmpty()) {
            int count = 0;
            for (ProductSku productSku : productSkuList) {
                productSku.setSkuCode(product.getId()+"_"+count);
                count++;
                productSku.setProductId(product.getId());
                productSku.setSkuName(product.getName()+productSku.getSkuSpec());
                productSkuMapper.insert(productSku);
            }
        }
    }

    //获取单个商品的图片详情列表，sku列表集合
    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        QueryWrapper<ProductDetails> queryWrapperDetails = new QueryWrapper<>();
        queryWrapperDetails.eq("product_id", id);
        ProductDetails productDetails = productDetailsMapper.selectOne(queryWrapperDetails);
        if(productDetails != null){
            product.setDetailsImageUrls(productDetails.getImageUrls());
        }
        QueryWrapper<ProductSku> queryWrapperSku = new QueryWrapper<>();
        queryWrapperSku.eq("product_id", id);
        List<ProductSku> productSkus = productSkuMapper.selectList(queryWrapperSku);
        if(productSkus != null && !productSkus.isEmpty()){
            product.setProductSkuList(productSkus);
        }
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Product product) {
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
        ProductDetails productDetails = new ProductDetails();
        //".*?spzx-bucket/([^,]+)" 会匹配从字符串开始到 spzx-bucket/ 后面直到逗号或字符串结束的所有内容，"$1" 表示用第一个捕获组的内容替换整个匹配的内容
        String detailsImageUrls = product.getDetailsImageUrls().replaceAll(".*?-bucket/([^,]+)", "$1");
        productDetails.setImageUrls(detailsImageUrls);
        productDetails.setUpdateTime(new Date());
        UpdateWrapper<ProductDetails> updateWrapperDetails = new UpdateWrapper<>();
        updateWrapperDetails.eq("product_id", product.getId());
        productDetailsMapper.update(productDetails,updateWrapperDetails);
        List<ProductSku> productSkuList = product.getProductSkuList();
        for (ProductSku productSku : productSkuList) {
            productSku.setUpdateTime(new Date());
            productSkuMapper.updateById(productSku);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        productMapper.deleteById(id);
        productDetailsMapper.delete(new QueryWrapper<ProductDetails>().eq("product_id", id));
        productSkuMapper.delete(new QueryWrapper<ProductSku>().eq("product_id", id));
    }

    @Override
    public void updateAuditStatus(Long id, Integer auditStatus) {
        Product product = new Product();
        product.setId(id);
        if(auditStatus == 1){
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        }else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批未通过");
        }
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if(status == 1) {
            product.setStatus(1);
        } else {
            product.setStatus(-1);
        }
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
    }
}
