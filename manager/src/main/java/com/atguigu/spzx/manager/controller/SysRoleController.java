package com.atguigu.spzx.manager.controller;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.service.SysRoleService;
import com.atguigu.spzx.model.dto.system.SysRoleDto;
import com.atguigu.spzx.model.entity.system.SysRole;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.SysRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    //查询分页角色列表
    @PostMapping("/findByPage/{current}/{limit}")
    public Result<SysRoleVo> findByPage(@PathVariable int current, @PathVariable int limit, @RequestBody SysRoleDto sysRoleDto) {
        SysRoleVo sysRoleVo = sysRoleService.findByPage(sysRoleDto,current,limit);
        return Result.build(sysRoleVo, ResultCodeEnum.SUCCESS);
    }

    //添加角色
    @PostMapping("/saveSysRole")
    public Result saveSysRole(@RequestBody SysRole sysRole) {
        sysRoleService.saveSysRole(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //修改角色
    @PutMapping("/updateSysRole")
    public Result updateSysRole(@RequestBody SysRole sysRole) {
        sysRoleService.updateSysRole(sysRole);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //删除角色
    @DeleteMapping("/deleteById/{roleId}")
    public Result deleteById(@PathVariable Long roleId) {
        sysRoleService.deleteById(roleId);
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //查询所有角色列表
    @GetMapping("/findAllRoles/{userId}")
    public Result<Map<String,Object>> findAllRoles(@PathVariable Long userId) {
        Map<String, Object> resultMaps = sysRoleService.findAllRoles(userId);
        return Result.build(resultMaps , ResultCodeEnum.SUCCESS);
    }
}
