package com.atguigu.spzx.manager;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.common.log.annotation.EnableLogAspect;
import com.atguigu.spzx.manager.properties.MinioProperties;
import com.atguigu.spzx.manager.properties.UserAuthProperties;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableLogAspect
@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu.spzx"})
@EnableConfigurationProperties(value = {UserAuthProperties.class, MinioProperties.class})
@MapperScan("com.atguigu.spzx.manager.mapper")
@EnableScheduling
public class ManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerApplication.class, args);
    }
    //Mybatis-plus插件加入到ioc容器
    @Bean
    public MybatisPlusInterceptor plusInterceptor(){
        //Mybatis-plus的插件集合【需要的插件加入到这个集合即可，分页插件】
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        //分页插件
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;
    }
}
/*
@EnableLogAspect:
这个注解通常用于启用日志切面（Log Aspect），通过AOP（面向切面编程）来记录方法的调用和执行情况。它可以帮助你自动记录日志，而不需要在每个方法中手动添加日志代码。

@SpringBootApplication:
这是一个组合注解，包含了@Configuration、@EnableAutoConfiguration和@ComponentScan。它标识一个Spring Boot应用的主配置类，并启用自动配置和组件扫描。

@ComponentScan(basePackages = {"com.atguigu.spzx"}):
这个注解告诉Spring框架扫描指定的包及其子包中的组件（如@Component、@Service、@Repository等注解的类），并将它们注册为Spring Bean⁶⁷。

@EnableConfigurationProperties(value = {UserAuthProperties.class, MinioProperties.class}):
这个注解启用对@ConfigurationProperties注解类的支持，并将指定的配置属性类注册为Spring Bean。它允许你将配置文件中的属性绑定到这些类的字段上[20]²¹。

@MapperScan("com.atguigu.spzx.manager.mapper"):
这个注解用于MyBatis框架，自动扫描指定包路径下的Mapper接口，并将它们注册为Spring Bean。这样，你就不需要手动为每个Mapper接口添加@Mapper注解¹²¹³。

@EnableScheduling:
这个注解启用Spring的定时任务执行功能。它允许你使用@Scheduled注解来声明定时任务，使得指定的方法可以按照设定的时间间隔或固定的时间点自动执行¹⁵¹⁶。
 */