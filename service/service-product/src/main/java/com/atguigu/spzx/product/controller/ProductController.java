package com.atguigu.spzx.product.controller;

import com.atguigu.spzx.model.dto.h5.ProductSkuDto;
import com.atguigu.spzx.model.dto.product.SkuSaleDto;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.ProductItemVo;
import com.atguigu.spzx.model.vo.h5.ProductSkuVo;
import com.atguigu.spzx.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable int page, @PathVariable int limit, ProductSkuDto productSkuDto) {
        ProductSkuVo productSkuVo = productService.findByPage(page,limit,productSkuDto);
        return Result.build(productSkuVo, ResultCodeEnum.SUCCESS);
    }

    //视频详情接口
    @GetMapping("/item/{skuId}")
    public Result item(@PathVariable Long skuId) {
        ProductItemVo productItemVo = productService.item(skuId);
        return Result.build(productItemVo, ResultCodeEnum.SUCCESS);
    }

    //远程调用，根据skuid获取商品信息
    @GetMapping("/getBySkuId/{skuId}")
    public ProductSku getBySkuId(@PathVariable Long skuId) {
        return productService.getBySkuId(skuId);
    }

    //远程调用 更新商品sku销量
    @PostMapping("updateSkuSaleNum")
    public Boolean updateSkuSaleNum(@RequestBody List<SkuSaleDto> skuSaleDtoList) {
        return productService.updateSkuSaleNum(skuSaleDtoList);
    }
}
