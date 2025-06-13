package com.atguigu.spzx.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.feign.cart.CartFeignClient;
import com.atguigu.spzx.feign.product.ProductFeignClient;
import com.atguigu.spzx.feign.user.UserFeignClient;
import com.atguigu.spzx.model.dto.h5.OrderInfoDto;
import com.atguigu.spzx.model.entity.h5.CartInfo;
import com.atguigu.spzx.model.entity.order.OrderInfo;
import com.atguigu.spzx.model.entity.order.OrderItem;
import com.atguigu.spzx.model.entity.order.OrderLog;
import com.atguigu.spzx.model.entity.product.ProductSku;
import com.atguigu.spzx.model.entity.user.UserAddress;
import com.atguigu.spzx.model.entity.user.UserInfo;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.h5.TradeVo;
import com.atguigu.spzx.order.mapper.OrderInfoMapper;
import com.atguigu.spzx.order.mapper.OrderItemMapper;
import com.atguigu.spzx.order.mapper.OrderLogMapper;
import com.atguigu.spzx.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderLogMapper orderLogMapper;

    @Override
    public TradeVo trade(String token) {
        List<CartInfo> allChecked = cartFeignClient.getAllChecked(token);
        List<OrderItem> orderItemList = new ArrayList<>();
        //获取订单支付总金额
        BigDecimal totalAmount = new BigDecimal(0);
        for(CartInfo cartInfo : allChecked){
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
            orderItem.setThumbImg(cartInfo.getImgUrl());
            orderItemList.add(orderItem);
        }
        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        return tradeVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrderInfoDto orderInfoDto, String token) {
        String info = stringRedisTemplate.opsForValue().get("user:login:" + token);
        if(!StringUtils.hasText(info)){
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        UserInfo userInfo = JSON.parseObject(info, UserInfo.class);
        //1 orderInfoDto获取所有订单项List List<OrderItem>
        List<OrderItem> orderItemList = orderInfoDto.getOrderItemList();
        //2 判断List<OrderItem>为空,抛出异常
        if(CollectionUtils.isEmpty(orderItemList)){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //3 校验商品库存是否充足
        // 遍历List<OrderItem>集合,得到每个orderItem
        for(OrderItem orderItem : orderItemList){
            // 远程调用获取商品sku信息,(包含库存量)
            ProductSku productSku = productFeignClient.getBySkuId(orderItem.getSkuId());
            if(productSku == null){
                throw new GuiguException(ResultCodeEnum.DATA_ERROR);
            }
            // 校验每个orderItem库存量是否充足
            if(orderItem.getSkuNum() > productSku.getStockNum()){
                throw new GuiguException(ResultCodeEnum.STOCK_LESS);
            }
        }
        //4 添加数据到order_info表
        // 封装数据到OrderInfo对象
        OrderInfo orderInfo = new OrderInfo();
        //订单编号
        orderInfo.setOrderNo(String.valueOf(System.currentTimeMillis()));
        //用户id
        orderInfo.setUserId(userInfo.getId());
        //用户昵称
        orderInfo.setNickName(userInfo.getNickName());
        //远程调用:获取用户收货地址信息
        UserAddress userAddress = userFeignClient.getUserAddress(orderInfoDto.getUserAddressId());
        orderInfo.setReceiverName(userAddress.getName());
        orderInfo.setReceiverPhone(userAddress.getPhone());
        orderInfo.setReceiverTagName(userAddress.getTagName());
        orderInfo.setReceiverProvince(userAddress.getProvinceCode());
        orderInfo.setReceiverCity(userAddress.getCityCode());
        orderInfo.setReceiverDistrict(userAddress.getDistrictCode());
        orderInfo.setReceiverAddress(userAddress.getFullAddress());
        //订单金额
        BigDecimal totalAmount = new BigDecimal(0);
        for (OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setCouponAmount(new BigDecimal(0));
        orderInfo.setOriginalTotalAmount(totalAmount);
        orderInfo.setFeightFee(orderInfoDto.getFeightFee());
        orderInfo.setPayType(2);
        orderInfo.setOrderStatus(0);
        orderInfoMapper.insert(orderInfo);
        //5 添加数据到order_item表
        //添加List<OrderItem>里面数据,把集合每个orderItem添加表
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderId(orderInfo.getId());
//            orderItem.setThumbImg(orderItem.getThumbImg().replaceAll(".*?-bucket/([^,]+)", "$1"));
            orderItemMapper.insert(orderItem);
        }
        //6 添加数据到order_Log表
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(0);
        orderLog.setNote("提交订单");
        orderLogMapper.insert(orderLog);
        //7 远程调用：把生成订单商品从购物车里面删除
        cartFeignClient.deleteChecked(token);
        //8 返回订单id
        return orderInfo.getId();
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        return orderInfoMapper.selectById(orderId);
    }

    @Override
    public TradeVo buy(Long skuId) {
        // 查询商品
        ProductSku productSku = productFeignClient.getBySkuId(skuId);
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(skuId);
        orderItem.setSkuName(productSku.getSkuName());
        orderItem.setSkuNum(1);
        orderItem.setSkuPrice(productSku.getSalePrice());
        orderItem.setThumbImg("http://192.168.200.129:9000/spzx-bucket/"+productSku.getThumbImg());
        orderItemList.add(orderItem);

        // 计算总金额
        BigDecimal totalAmount = productSku.getSalePrice();
        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);

        // 返回
        return tradeVo;
    }

    @Override
    public PageInfo<OrderInfo> findUserPage(Integer page, Integer limit, Integer orderStatus, String token) {
        PageHelper.startPage(page, limit);
        String info = stringRedisTemplate.opsForValue().get("user:login:" + token);
        if(!StringUtils.hasText(info)){
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        UserInfo userInfo = JSON.parseObject(info, UserInfo.class);
        Long userId = userInfo.getId();
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getUserId, userId)
                .eq(orderStatus != null, OrderInfo::getOrderStatus, orderStatus)
                .orderByDesc(OrderInfo::getCreateTime);
        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(orderInfoLambdaQueryWrapper);

        orderInfoList.forEach(orderInfo -> {
            LambdaQueryWrapper<OrderItem> orderItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderItemLambdaQueryWrapper.eq(OrderItem::getOrderId, orderInfo.getId());
            List<OrderItem> orderItem = orderItemMapper.selectList(orderItemLambdaQueryWrapper);
            orderInfo.setOrderItemList(orderItem);
        });
        return new PageInfo<>(orderInfoList);
    }

    @Override
    public OrderInfo getOrderInfoByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoLambdaQueryWrapper);
        LambdaQueryWrapper<OrderItem> orderItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderItemLambdaQueryWrapper.eq(OrderItem::getOrderId, orderInfo.getId());
        List<OrderItem> orderItemList = orderItemMapper.selectList(orderItemLambdaQueryWrapper);
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }

    @Transactional
    @Override
    public void updateOrderStatus(String orderNo) {

        // 更新订单状态
        LambdaQueryWrapper<OrderInfo> orderInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderInfoLambdaQueryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoLambdaQueryWrapper);
        orderInfo.setOrderStatus(1);
        orderInfo.setPaymentTime(new Date());
        orderInfoMapper.updateById(orderInfo);

        // 记录日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderInfo.getId());
        orderLog.setProcessStatus(1);
        orderLog.setNote("支付宝支付成功");
        orderLogMapper.insert(orderLog);
    }
}
