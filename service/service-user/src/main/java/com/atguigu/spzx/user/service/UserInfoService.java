package com.atguigu.spzx.user.service;

import com.atguigu.spzx.model.dto.h5.UserLoginDto;
import com.atguigu.spzx.model.dto.h5.UserRegisterDto;
import com.atguigu.spzx.model.vo.h5.UserInfoVo;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
public interface UserInfoService {
    void register(UserRegisterDto userRegisterDto);
    String login(UserLoginDto userLoginDto);
    UserInfoVo getCurrentUserInfo(String token);
    void logout(String token);
}
