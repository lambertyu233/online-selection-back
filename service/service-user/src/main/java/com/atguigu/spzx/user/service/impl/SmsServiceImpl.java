package com.atguigu.spzx.user.service.impl;

import com.atguigu.spzx.user.service.SmsService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendCode(String phone) {
        //先判断手机之前是否重复发过未过期的验证码
        String code = stringRedisTemplate.opsForValue().get(phone);
        if(StringUtils.hasText(code)){
            return;
        }
        //1 生成验证码
        code = RandomStringUtils.randomNumeric(4);
        System.out.println("手机验证码为：" + code);
        //2 把验证码放进redis
        stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
        //3 向手机号发送验证码（以后买了服务再来添加）
    }
}
