package com.atguigu.spzx.manager.mapper;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.order.OrderInfo;
import com.atguigu.spzx.model.entity.order.OrderStatistics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    OrderStatistics selectStatisticsByDate(String createDate);

}
