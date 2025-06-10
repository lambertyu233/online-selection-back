package com.atguigu.spzx.feign.user;

import com.atguigu.spzx.model.entity.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {

    @GetMapping("api/user/userAddress/getUserAddress/{id}")
    UserAddress getUserAddress(@PathVariable("id") Long id);
}
