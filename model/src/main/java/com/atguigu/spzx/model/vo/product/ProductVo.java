package com.atguigu.spzx.model.vo.product;

import com.atguigu.spzx.model.entity.product.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductVo {

    private List<Product> list;

    private Long total;
}
