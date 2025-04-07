package com.atguigu.spzx.common.log.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.system.SysOperLog;

public interface AsyncOperLogService {

    void SaveSysOperLog(SysOperLog sysOperLog);
}
