package com.atguigu.spzx.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@SpringBootApplication
@MapperScan("com.atguigu.spzx.order.mapper")
@EnableFeignClients(basePackages = {"com.atguigu.spzx"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
