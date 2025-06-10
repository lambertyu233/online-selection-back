package com.atguigu.spzx.feign.cart;

import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.vo.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@FeignClient(value = "service-cart")
public interface CartFeignClient {

    @GetMapping("/api/order/cart/auth/getAllChecked")
    List<CartInfo> getAllChecked(@RequestHeader("token") String token);

    @GetMapping("/api/order/cart/auth/deleteChecked")
    Result deleteChecked(@RequestHeader("token") String token);
}
