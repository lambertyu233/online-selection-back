package com.atguigu.spzx.cart.controller;

import com.atguigu.spzx.cart.service.CartService;
import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("api/order/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("auth/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId, @PathVariable Integer skuNum, HttpServletRequest request) {
        String token = request.getHeader("token");
        cartService.addToCart(skuId,skuNum,token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("auth/cartList")
    public Result cartList(HttpServletRequest request) {
        String token = request.getHeader("token");
        List<CartInfo> cartInfoList = cartService.cartList(token);
        return Result.build(cartInfoList, ResultCodeEnum.SUCCESS);
    }

    @DeleteMapping("auth/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable Long skuId, HttpServletRequest request) {
        String token = request.getHeader("token");
        cartService.deleteCart(skuId,token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/auth/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId, @PathVariable Integer isChecked, HttpServletRequest request) {
        String token = request.getHeader("token");
        cartService.checkCart(skuId,isChecked,token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/auth/allCheckCart/{isChecked}")
    public Result allCheckCart(@PathVariable Integer isChecked, HttpServletRequest request) {
        String token = request.getHeader("token");
        cartService.allCheckCart(isChecked,token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    @GetMapping("/auth/clearCart")
    public Result clearCart(HttpServletRequest request) {
        String token = request.getHeader("token");
        cartService.clearCart(token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //远程调用：订单结算使用，获取购物车选中商品列表
    @GetMapping("/auth/getAllChecked")
    public List<CartInfo> getAllChecked(@RequestHeader("token") String token) {
        return cartService.getAllChecked(token);
    }

    //远程调用：删除生成订单的购物车商品
    @GetMapping("/auth/deleteChecked")
    public Result deleteChecked(@RequestHeader("token") String token) {
        cartService.deleteChecked(token);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }
}