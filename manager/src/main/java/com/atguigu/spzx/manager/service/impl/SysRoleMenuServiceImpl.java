package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.manager.mapper.SysMenuMapper;
import com.atguigu.spzx.manager.mapper.SysRoleMenuMapper;
import com.atguigu.spzx.manager.service.SysRoleMenuService;
import com.atguigu.spzx.manager.utils.MenuHelper;
import com.atguigu.spzx.model.dto.system.AssignMenuDto;
import com.atguigu.spzx.model.entity.system.SysMenu;
import com.atguigu.spzx.model.entity.system.SysRoleMenu;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public Map<String, Object> findSysRoleMenuByRoleId(Integer roleId) {
        //全部的菜单
        List<SysMenu> sysMenuList = MenuHelper.buildTree(sysMenuMapper.selectList(null));
        //该角色已经分配过的菜单
        List<Long> roleMenuIds = new ArrayList<>();
        QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId).eq("is_half",0);
        List<SysRoleMenu> roleIds = sysRoleMenuMapper.selectList(queryWrapper);
        for(SysRoleMenu sysRoleMenu : roleIds){
            roleMenuIds.add(sysRoleMenu.getMenuId());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("sysMenuList", sysMenuList);
        map.put("roleMenuIds", roleMenuIds);
        return map;
    }

    @Override
    public void doAssign(AssignMenuDto assignMenuDto) {
        //删除角色之前分配过的菜单数据
        Long roleId = assignMenuDto.getRoleId();
        QueryWrapper<SysRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        sysRoleMenuMapper.delete(queryWrapper);
        //保存分配的数据
        List<Map<String, Number>> menuInfo = assignMenuDto.getMenuIdList();
        for(Map<String, Number> map : menuInfo){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(map.get("id").longValue());
            sysRoleMenu.setIsHalf(map.get("isHalf").intValue());
            sysRoleMenu.setUpdateTime(new Date());
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }
}
