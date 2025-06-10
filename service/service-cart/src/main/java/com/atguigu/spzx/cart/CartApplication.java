package com.atguigu.spzx.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@SpringBootApplication( exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients(basePackages = {"com.atguigu.spzx"})
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }
}
