package com.atguigu.spzx.order.service;

import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.vo.h5.TradeVo;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
public interface OrderInfoService {
    TradeVo trade(String token);
    Long submitOrder(OrderInfoDto orderInfoDto, String token);
}
