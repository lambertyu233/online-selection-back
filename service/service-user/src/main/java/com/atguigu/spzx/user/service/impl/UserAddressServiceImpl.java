package com.atguigu.spzx.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.spzx.model.entity.user.UserAddress;
import com.atguigu.spzx.model.entity.user.UserInfo;
import com.atguigu.spzx.user.mapper.UserAddressMapper;
import com.atguigu.spzx.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<UserAddress> findUserAddressList(String token) {
        String userJson = stringRedisTemplate.opsForValue().get("user:login:" + token);
        UserInfo userInfo = JSONObject.parseObject(userJson, UserInfo.class);
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userInfo.getId());
        return userAddressMapper.selectList(queryWrapper);
    }

    @Override
    public UserAddress getUserAddress(Long id) {
        return userAddressMapper.selectById(id);
    }
}
