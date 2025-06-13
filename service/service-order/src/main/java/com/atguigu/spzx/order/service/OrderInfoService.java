package com.atguigu.spzx.order.service;

import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.entity.order.OrderInfo;
import com.atguigu.spzx.model.vo.h5.TradeVo;
import com.github.pagehelper.PageInfo;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
public interface OrderInfoService {
    TradeVo trade(String token);
    Long submitOrder(OrderInfoDto orderInfoDto, String token);
    OrderInfo getOrderInfo(Long orderId);
    TradeVo buy(Long skuId);
    PageInfo<OrderInfo> findUserPage(Integer page, Integer limit, Integer orderStatus, String token);
    OrderInfo getOrderInfoByOrderNo(String orderNo);
    void updateOrderStatus(String orderNo);
}
