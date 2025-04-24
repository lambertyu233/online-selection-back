package com.atguigu.spzx.gateway;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu.spzx.utils", "com.atguigu.spzx.gateway"})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
