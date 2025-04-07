package com.atguigu.spzx.model.vo.product;

import com.atguigu.spzx.model.entity.product.Brand;
import lombok.Data;

import java.util.List;

@Data
public class BrandVo {

    private List<Brand> brandList;

    private Long pageTotal;
}
