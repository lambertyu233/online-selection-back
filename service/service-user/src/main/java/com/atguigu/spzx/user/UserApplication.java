package com.atguigu.spzx.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@SpringBootApplication
@MapperScan("com.atguigu.spzx.user.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
