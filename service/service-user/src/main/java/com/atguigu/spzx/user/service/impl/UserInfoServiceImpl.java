package com.atguigu.spzx.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.model.dto.h5.UserLoginDto;
import com.atguigu.spzx.model.dto.h5.UserRegisterDto;
import com.atguigu.spzx.model.entity.user.UserInfo;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.UserInfoVo;
import com.atguigu.spzx.user.mapper.UserInfoMapper;
import com.atguigu.spzx.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void register(UserRegisterDto userRegisterDto) {
        String code = stringRedisTemplate.opsForValue().get(userRegisterDto.getUsername());
        System.out.println(code);
        if(code == null || !code.equals(userRegisterDto.getCode())){
            throw new GuiguException(ResultCodeEnum.VALIDATECODE_ERROR);
        }
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUsername, userRegisterDto.getUsername());
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        if(userInfo != null){
            throw new GuiguException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setUsername(userRegisterDto.getUsername());
        newUserInfo.setPassword(DigestUtils.md5DigestAsHex(userRegisterDto.getPassword().getBytes()));
        newUserInfo.setNickName(userRegisterDto.getNickName());
        newUserInfo.setPhone(userRegisterDto.getUsername());
        newUserInfo.setSex(0);
        newUserInfo.setStatus(1);
        newUserInfo.setAvatar("http://192.168.200.129:9000/spzx-bucket/special/defaultavatar.jpg");
        userInfoMapper.insert(newUserInfo);
        stringRedisTemplate.delete(userRegisterDto.getUsername());
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUsername, userLoginDto.getUsername());
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
        if(userInfo == null){
            throw new GuiguException(ResultCodeEnum.NOREGISTER_ERROR);
        }
        if(!userInfo.getPassword().equals(DigestUtils.md5DigestAsHex(userLoginDto.getPassword().getBytes()))){
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        //检查用户是否禁用
        if(userInfo.getStatus() == 0){
            throw new GuiguException(ResultCodeEnum.ACCOUNT_STOP);
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        stringRedisTemplate.opsForValue().set("user:login:"+token, JSON.toJSONString(userInfo),30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public UserInfoVo getCurrentUserInfo(String token) {
        String info = stringRedisTemplate.opsForValue().get("user:login:" + token);
        if(!StringUtils.hasText(info)){
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        UserInfo userInfo = JSON.parseObject(info, UserInfo.class);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setNickName(userInfo.getNickName());
        userInfoVo.setAvatar(userInfo.getAvatar());
        return userInfoVo;
    }

    @Override
    public void logout(String token) {
        if(token == null || token.isEmpty()){
            return;
        }
        stringRedisTemplate.delete("user:login:" + token);
    }
}
