package com.atguigu.spzx.manager.mapper;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.system.SysRoleUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface SysRoleUserMapper extends BaseMapper<SysRoleUser> {
    //查询该角色的所有角色
    List<String> selectRoleCodesByUserId(Long userId);
}
