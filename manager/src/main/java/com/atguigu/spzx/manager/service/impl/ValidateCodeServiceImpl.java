package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.atguigu.spzx.manager.service.ValidateCodeService;
import com.atguigu.spzx.model.vo.system.ValidateCodeVo;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //生成图片验证码
    @Override
    public ValidateCodeVo generateValidateCode() {
        //1 通过工具 hutool 生成图片验证码
        //150：宽度，48：高度，4：验证码值数量，2：验证码划痕数量，干扰程度
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 48, 4, 2);
        String codeValue = circleCaptcha.getCode();//4位验证码值
        String imageBase64 = circleCaptcha.getImageBase64();//返回图片验证码，对图片进行了base64编码
        //2 把验证码存储到redis里，设置redis的key：uuid   redis的value：验证码值
        //设置过期时间
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        stringRedisTemplate.opsForValue().set("user:validate"+key,codeValue,
                                        5, TimeUnit.MINUTES);//过期时间5，单位分钟
        //返回ValidateCodeVo对象
        ValidateCodeVo validateCodeVo = new ValidateCodeVo();
        validateCodeVo.setCodeKey(key);//redis中存储数据的key
        validateCodeVo.setCodeValue("data:image/png;base64," + imageBase64);//是要把图片设置进去，格式固定，这个数据会在页面上直接通过图片的形式显示出来
        return validateCodeVo;
    }
}
