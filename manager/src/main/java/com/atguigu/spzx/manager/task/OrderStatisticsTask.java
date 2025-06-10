package com.atguigu.spzx.manager.task;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import cn.hutool.core.date.DateUtil;
import com.atguigu.spzx.manager.mapper.OrderInfoMapper;
import com.atguigu.spzx.manager.mapper.OrderStatisticsMapper;
import com.atguigu.spzx.model.entity.order.OrderStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OrderStatisticsTask {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderStatisticsMapper orderStatisticsMapper;

    //每天凌晨2点，查询前一天统计数据，把统计之后的数据添加到统计结果表里
    //@Scheduled(cron = "0/10 8 * * * ?")//测试用，每十秒一次
    @Scheduled(cron = "0 0 2 * * ?")
    public void orderTotalAmountStatistics(){
        //1 获取前一天的日期
        String createDate = DateUtil.offsetDay(new Date(), -1).toString("yyyy-MM-dd");
        //2 根据前一天日期进行统计功能
        OrderStatistics orderStatistics = orderInfoMapper.selectStatisticsByDate(createDate);
        //3 把统计之后的数据添加到统计结果表里面
        if(orderStatistics != null){
            orderStatisticsMapper.insert(orderStatistics);
        }
    }
}
