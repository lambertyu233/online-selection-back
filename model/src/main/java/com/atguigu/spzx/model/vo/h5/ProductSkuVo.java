package com.atguigu.spzx.model.vo.h5;

import com.atguigu.spzx.model.entity.product.ProductSku;
import lombok.Data;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Data
public class ProductSkuVo {

    private List<ProductSku> list;

    private Long total;
}
