package com.atguigu.spzx.model.vo.product;

import com.atguigu.spzx.model.entity.product.CategoryBrand;
import lombok.Data;

import java.util.List;

@Data
public class CategoryBrandVo {

    private List<CategoryBrand> categoryBrands;

    private Long pageTotal;
}
