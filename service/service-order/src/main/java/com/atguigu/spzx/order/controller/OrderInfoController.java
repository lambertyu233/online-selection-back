package com.atguigu.spzx.order.controller;

import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.entity.order.OrderInfo;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.TradeVo;
import com.github.pagehelper.PageInfo;
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

    //从购物车结算生成订单
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfoDto orderInfoDto, @RequestHeader("token") String token) {
        Long orderId = orderInfoService.submitOrder(orderInfoDto, token);
        return Result.build(orderId, ResultCodeEnum.SUCCESS);
    }

    //获取订单信息
    @GetMapping("auth/{orderId}")
    public Result getOrderInfo(@PathVariable("orderId") Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return Result.build(orderInfo, ResultCodeEnum.SUCCESS);
    }

    //单一商品立即购买生成订单
    @GetMapping("auth/buy/{skuId}")
    public Result buy(@PathVariable("skuId") Long skuId) {
        TradeVo tradeVo = orderInfoService.buy(skuId);
        return Result.build(tradeVo, ResultCodeEnum.SUCCESS);
    }

    //获取订单分页列表
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable("page") Integer page,
                       @PathVariable("limit") Integer limit,
                       @RequestParam(required = false) Integer orderStatus,
                       @RequestHeader("token") String token) {
        PageInfo<OrderInfo> pageInfo = orderInfoService.findUserPage(page, limit, orderStatus, token);
        return Result.build(pageInfo, ResultCodeEnum.SUCCESS);
    }

    //远程调用 根据订单编号获取订单信息
    @GetMapping("auth/getOrderInfoByOrderNo/{orderNo}")
    public OrderInfo getOrderInfoByOrderNo(@PathVariable("orderNo") String orderNo) {
        return orderInfoService.getOrderInfoByOrderNo(orderNo);
    }

    //远程调用 更新订单状态
    @GetMapping("auth/updateOrderStatusPayed/{orderNo}")
    public Result updateOrderStatus(@PathVariable(value = "orderNo") String orderNo) {
        orderInfoService.updateOrderStatus(orderNo);
        return Result.build(null , ResultCodeEnum.SUCCESS);
    }

}
