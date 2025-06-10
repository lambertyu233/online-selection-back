package com.atguigu.spzx.cart.service;

import com.atguigu.spzx.model.entity.h5.CartInfo;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
public interface CartService {
    void addToCart(Long skuId, Integer skuNum ,String token);
    List<CartInfo> cartList(String token);
    void deleteCart(Long skuId, String token);
    void checkCart(Long skuId, Integer isChecked, String token);
    void allCheckCart(Integer isChecked, String token);
    void clearCart(String token);
    List<CartInfo> getAllChecked(String token);
    void deleteChecked(String token);
}
