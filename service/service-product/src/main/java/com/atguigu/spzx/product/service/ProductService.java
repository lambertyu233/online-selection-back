package com.atguigu.spzx.product.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.h5.ProductItemVo;
import com.atguigu.spzx.model.vo.h5.ProductSkuVo;

import java.util.List;

public interface ProductService {
    List<ProductSku> selectProductSkuBySal();

    ProductSkuVo findByPage(int page, int limit, ProductSkuDto productSkuDto);

    ProductItemVo item(Long skuId);

    ProductSku getBySkuId(Long skuId);

}
