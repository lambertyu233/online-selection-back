package com.atguigu.spzx.model.vo.product;

import com.atguigu.spzx.model.entity.product.ProductSpec;
import lombok.Data;

import java.util.List;

@Data
public class ProductSpecVo {

    private List<ProductSpec> productSpecs;

    private Long pageTotal;
}
