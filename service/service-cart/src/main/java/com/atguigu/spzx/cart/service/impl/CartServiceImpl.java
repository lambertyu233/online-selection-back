package com.atguigu.spzx.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.spzx.cart.service.CartService;
import com.atguigu.spzx.feign.product.ProductFeignClient;
import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.entity.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public void addToCart(Long skuId, Integer skuNum ,String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        //构建hash类型的key
        String cartKey = "user:cart:" + userInfo.getId();
        //从redis里面获取购物车里面skuId商品数据
        Object cartInfoObj = stringRedisTemplate.opsForHash().get(cartKey, String.valueOf(skuId));
        CartInfo cartInfo = null;
        //如果购物车存在添加的商品，则商品数量相加
        if(cartInfoObj != null){
            cartInfo = JSONObject.parseObject(cartInfoObj.toString(), CartInfo.class);
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            //设置选中状态
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        }else {
            cartInfo = new CartInfo();
            //远程调用实现
            ProductSku productSku = productFeignClient.getBySkuId(skuId);
            cartInfo.setCartPrice(productSku.getSalePrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userInfo.getId());
            cartInfo.setImgUrl(productSku.getThumbImg());
            cartInfo.setSkuName(productSku.getSkuName());
            cartInfo.setIsChecked(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }
        //添加到redis里面
        stringRedisTemplate.opsForHash().put(cartKey, String.valueOf(skuId), JSON.toJSONString(cartInfo));
    }

    @Override
    public List<CartInfo> cartList(String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        List<Object> values = stringRedisTemplate.opsForHash().values(cartKey);
        if(CollectionUtils.isEmpty(values)){
            return List.of();
        }
        List<CartInfo> cartInfoList = values.stream()
                .map(cartInfoObj -> JSONObject.parseObject(cartInfoObj.toString(), CartInfo.class))
                .map(cartInfo -> {
                    cartInfo.setImgUrl("http://192.168.200.129:9000/spzx-bucket/" + cartInfo.getImgUrl());
                    return cartInfo;
                })
                .sorted(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())))
                .toList();
        return cartInfoList;
    }

    @Override
    public void deleteCart(Long skuId, String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        stringRedisTemplate.opsForHash().delete(cartKey, String.valueOf(skuId));
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked, String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        Boolean hasKey = stringRedisTemplate.opsForHash().hasKey(cartKey, String.valueOf(skuId));
        if(!hasKey){
            return;
        }
        String cartInfoString = stringRedisTemplate.opsForHash().get(cartKey, String.valueOf(skuId)).toString();
        CartInfo cartInfo = JSON.parseObject(cartInfoString, CartInfo.class);
        cartInfo.setIsChecked(isChecked);
        stringRedisTemplate.opsForHash().put(cartKey, String.valueOf(skuId), JSON.toJSONString(cartInfo));
    }

    @Override
    public void allCheckCart(Integer isChecked, String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        List<Object> values = stringRedisTemplate.opsForHash().values(cartKey);
        values.stream()
                .map(cartInfoObj -> JSONObject.parseObject(cartInfoObj.toString(), CartInfo.class))
                .forEach(cartInfo -> {
                    cartInfo.setIsChecked(isChecked);
                    stringRedisTemplate.opsForHash().put(
                            cartKey,
                            String.valueOf(cartInfo.getSkuId()),
                            JSON.toJSONString(cartInfo)
                    );
                });
    }

    @Override
    public void clearCart(String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public List<CartInfo> getAllChecked(String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        List<Object> values = stringRedisTemplate.opsForHash().values(cartKey);
        if(CollectionUtils.isEmpty(values)){
            return List.of();
        }
        List<CartInfo> cartInfoList = values.stream()
                .map(cartInfoObj -> JSONObject.parseObject(cartInfoObj.toString(), CartInfo.class))
                .map(cartInfo -> {
                    cartInfo.setImgUrl("http://192.168.200.129:9000/spzx-bucket/" + cartInfo.getImgUrl());
                    return cartInfo;
                })
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .sorted(((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())))
                .toList();
        return cartInfoList;
    }

    @Override
    public void deleteChecked(String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        String cartKey = "user:cart:" + userInfo.getId();
        List<Object> values = stringRedisTemplate.opsForHash().values(cartKey);
        if(CollectionUtils.isEmpty(values)){
            return;
        }
        values.stream()
                .map(cartInfoObj -> JSONObject.parseObject(cartInfoObj.toString(), CartInfo.class))
                .forEach(cartInfo -> {
                    if(cartInfo.getIsChecked() == 1){
                        stringRedisTemplate.opsForHash().delete(
                                cartKey,
                                String.valueOf(cartInfo.getSkuId())
                        );
                    }
                });
    }
}
