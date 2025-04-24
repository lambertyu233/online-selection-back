package com.atguigu.spzx.manager.config;

import com.atguigu.spzx.manager.filter.HeaderAuthFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
@EnableReactiveMethodSecurity // 启用方法级权限控制
public class SecurityConfig {

    @Resource
    private  HeaderAuthFilter headerAuthFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/system/sysUser/**").hasAnyAuthority("ptgly","yhgly")
                        .requestMatchers("/admin/system/sysRole/**","/admin/system/sysRoleMenu/**","/admin/system/sysMenu/**").hasAuthority("pygly")
                        .requestMatchers("/admin/product/**").hasAnyAuthority("ptgly","yhgly","xsry")
                        .anyRequest().permitAll()  // 其它接口使用注解的方式进行接口权限规定，这里全部放开
                )
                .csrf(AbstractHttpConfigurer::disable);

        http.addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
