package com.atguigu.spzx.user.service;

import com.atguigu.spzx.model.entity.user.UserAddress;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
public interface UserAddressService {
    List<UserAddress> findUserAddressList(String token);
    UserAddress getUserAddress(Long id);
}
