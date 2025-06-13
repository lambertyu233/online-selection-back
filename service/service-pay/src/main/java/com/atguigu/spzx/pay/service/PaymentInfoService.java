package com.atguigu.spzx.pay.service;

import com.atguigu.spzx.model.entity.pay.PaymentInfo;

import java.util.Map;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
public interface PaymentInfoService {

    PaymentInfo savePaymentInfo(String orderNo);
    void updatePaymentStatus(Map<String, String> paramMap);
}
