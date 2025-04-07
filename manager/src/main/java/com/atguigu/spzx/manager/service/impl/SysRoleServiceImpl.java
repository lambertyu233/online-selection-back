package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.mapper.SysRoleMapper;
import com.atguigu.spzx.manager.mapper.SysRoleUserMapper;
import com.atguigu.spzx.manager.service.SysRoleService;
import com.atguigu.spzx.model.dto.system.SysRoleDto;
import com.atguigu.spzx.model.entity.system.SysRole;
import com.atguigu.spzx.model.entity.system.SysRoleUser;
import com.atguigu.spzx.model.vo.system.SysRoleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class SysRoleServiceImpl implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Override
    public SysRoleVo findByPage(SysRoleDto sysRoleDto, int current, int limit) {
        //搜索功能的实现
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        //方案1手动判断
        if(sysRoleDto.getRoleName() != null && !"".equals(sysRoleDto.getRoleName())){
            queryWrapper.like("role_name", sysRoleDto.getRoleName());
        }
        //方案2: 拼接condition判断
        //eq(condition,列名,值)
//        queryWrapper.like(sysRoleDto.getRoleName() != null && !"".equals(sysRoleDto.getRoleName()),
//                "role_name", sysRoleDto.getRoleName());
        //current页码，limit页容量
        Page<SysRole> page = new Page<>(current, limit);
        sysRoleMapper.selectPage(page,queryWrapper);
        //当前页的数据
        SysRoleVo sysRoleVo = new SysRoleVo();
        sysRoleVo.setSysRoles(page.getRecords());
        sysRoleVo.setPageTotal(page.getTotal());
        return sysRoleVo;
    }

    @Override
    public void saveSysRole(SysRole sysRole) {
        sysRoleMapper.insert(sysRole);
    }

    @Override
    public void updateSysRole(SysRole sysRole) {
        Date currentDate = new Date();
        sysRole.setUpdateTime(currentDate);
        sysRoleMapper.updateById(sysRole);
    }

    @Override
    public void deleteById(Long roleId) {
        sysRoleMapper.deleteById(roleId);
    }

    @Override
    public Map<String, Object> findAllRoles(Long userId) {
        // 1 查询所有角色
        List<SysRole> sysRoles = sysRoleMapper.selectList(null);
        //2 分配过的角色列表
        List<SysRoleUser> roleUserList = sysRoleUserMapper.selectList(new QueryWrapper<SysRoleUser>().eq("user_id", userId));
        List<Long> roleIds = new ArrayList<>();
        for(SysRoleUser role:roleUserList){
            roleIds.add(role.getRoleId());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("sysRoles", sysRoles);
        map.put("sysUserRoles", roleIds);
        return map;
    }

}
