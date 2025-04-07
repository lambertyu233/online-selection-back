package com.atguigu.spzx.utils;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.system.SysUser;

public class AuthContextUtil {

    //创建threadLocal对象
    private static final ThreadLocal<SysUser> threadLocal = new ThreadLocal<>();

    //添加数据
    public static void set(SysUser sysuser) {
        threadLocal.set(sysuser);
    }

    //获取数据
    public static SysUser get() {
        return threadLocal.get();
    }

    //删除数据
    public static void remove() {
        threadLocal.remove();
    }
}
