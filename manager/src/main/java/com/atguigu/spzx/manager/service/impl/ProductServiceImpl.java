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

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductDetailsMapper productDetailsMapper;

    @Override
    public ProductVo findByPage(int page, int limit, ProductDto productDto) {
        Page<Product> productPage = new Page<>(page, limit);
        productMapper.findByPage(productPage,productDto);
        ProductVo productVo = new ProductVo();
        productVo.setList(productPage.getRecords());
        productVo.setTotal(productPage.getTotal());
        return productVo;
    }

    @Override
    public void save(Product product) {
        productMapper.insert(product);
        String detailsImageUrls = product.getDetailsImageUrls();
        if (detailsImageUrls != null && !detailsImageUrls.equals("")) {
            ProductDetails productDetails = new ProductDetails();
            productDetails.setProductId(product.getId());
            productDetails.setImageUrls(detailsImageUrls);
            productDetailsMapper.insert(productDetails);
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
    public void updateById(Product product) {
        product.setUpdateTime(new Date());
        productMapper.updateById(product);
        ProductDetails productDetails = new ProductDetails();
        String detailsImageUrls = product.getDetailsImageUrls();
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
