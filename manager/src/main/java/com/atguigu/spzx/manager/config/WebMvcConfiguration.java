package com.atguigu.spzx.manager.config;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.interceptor.LoginAuthInterceptor;
import com.atguigu.spzx.manager.properties.UserAuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private LoginAuthInterceptor loginAuthInterceptor;
    @Autowired
    private UserAuthProperties userAuthProperties;

    //拦截器注册
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginAuthInterceptor)
//                .excludePathPatterns("/admin/system/index/login",
//                        "/admin/system/index/generateValidateCode")
                .excludePathPatterns(userAuthProperties.getNoAuthUrls())
                .addPathPatterns("/**");
    }

    //跨域  在网关配置里面统一解决了跨域，这里就不用重复解决了
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")      // 添加路径规则
//                .allowCredentials(true)               // 是否允许在跨域的情况下传递Cookie
//                .allowedOriginPatterns("*")           // 允许请求来源的域规则
//                .allowedMethods("*")
//                .allowedHeaders("*") ;                // 允许所有的请求头
//    }
}
