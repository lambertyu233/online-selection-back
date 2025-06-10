package com.atguigu.spzx.order.controller;

import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.TradeVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.atguigu.spzx.order.service.OrderInfoService;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    //购物车结算接口
    @GetMapping("auth/trade")
    public Result trade(HttpServletRequest request) {
        String token = request.getHeader("token");
        TradeVo tradeVo = orderInfoService.trade(token);
        return Result.build(tradeVo, ResultCodeEnum.SUCCESS);
    }

    //生成订单
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfoDto orderInfoDto, @RequestHeader("token") String token) {
        Long orderId = orderInfoService.submitOrder(orderInfoDto, token);
        return Result.build(orderId, ResultCodeEnum.SUCCESS);
    }
}
