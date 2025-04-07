package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import cn.hutool.core.date.DateUtil;
import com.atguigu.spzx.manager.mapper.OrderStatisticsMapper;
import com.atguigu.spzx.manager.service.OrderStatisticsService;
import com.atguigu.spzx.model.dto.order.OrderStatisticsDto;
import com.atguigu.spzx.model.entity.order.OrderStatistics;
import com.atguigu.spzx.model.vo.order.OrderStatisticsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderStatisticsServiceImpl implements OrderStatisticsService {

    @Autowired
    private OrderStatisticsMapper orderStatisticsMapper;

    @Override
    public OrderStatisticsVo getOrderStatisticsData(OrderStatisticsDto orderStatisticsDto) {
        QueryWrapper<OrderStatistics> queryWrapper = new QueryWrapper<>();
        if(orderStatisticsDto.getCreateTimeBegin() != ""&&orderStatisticsDto.getCreateTimeBegin() != null){
            queryWrapper.ge("create_time",orderStatisticsDto.getCreateTimeBegin());
        }
        if(orderStatisticsDto.getCreateTimeEnd() != ""&&orderStatisticsDto.getCreateTimeEnd() != null){
            queryWrapper.le("create_time",orderStatisticsDto.getCreateTimeEnd());
        }
        List<OrderStatistics> orderStatisticsList = orderStatisticsMapper.selectList(queryWrapper);
        List<String> dateList = new ArrayList<>();
        List<BigDecimal> amountList = new ArrayList<>();
        for(OrderStatistics orderStatistics: orderStatisticsList){
            //方法1
//            dateList.add(orderStatistics.getOrderDate().toString());
            //方法2
            dateList.add(DateUtil.format(orderStatistics.getOrderDate(), "yyyy-MM-dd"));
            amountList.add(orderStatistics.getTotalAmount());
        }
        //方法3
//        List<String> dateList = orderStatisticsList.stream()
//                .map(orderStatistics -> DateUtil.format(orderStatistics.getOrderDate(), "yyyy-MM-dd"))
//                .collect(Collectors.toList());
        OrderStatisticsVo orderStatisticsVo = new OrderStatisticsVo();
        orderStatisticsVo.setDateList(dateList);
        orderStatisticsVo.setAmountList(amountList);
        return orderStatisticsVo;
    }
}
