package com.atguigu.spzx.manager.service;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.dto.system.AssignRoleDto;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.dto.system.SysUserDto;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.system.LoginVo;
import com.atguigu.spzx.model.vo.system.SysUserVo;


public interface SysUserService {

    /**
     * 根据用户名查询用户数据
     * @return
     */
    LoginVo login(LoginDto loginDto) ;

//    SysUser getUserInfo(String token);

    void logout(String token);

    SysUserVo findByPage(int pageNum, int pageSize, SysUserDto sysUserDto);

    void saveSysUser(SysUser sysUser);

    void updateSysUser(SysUser sysUser);

    void deleteById(int userId);

    void doAssign(AssignRoleDto assignRoleDto);

}