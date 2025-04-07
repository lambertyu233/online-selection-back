package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysMenuMapper;
import com.atguigu.spzx.manager.mapper.SysRoleMenuMapper;
import com.atguigu.spzx.manager.service.SysMenuService;
import com.atguigu.spzx.manager.utils.MenuHelper;
import com.atguigu.spzx.model.entity.system.SysMenu;
import com.atguigu.spzx.model.entity.system.SysRoleMenu;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.SysMenuVo;
import com.atguigu.spzx.utils.AuthContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        //1 查询所有菜单，返回list集合
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_value"); // 按照 sort_value 列升序排序
        List<SysMenu> sysMenuList = sysMenuMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(sysMenuList)){
            return null;
        }
        //2 调用工具类的方法。把返回list集合封装成要求的数据格式
        List<SysMenu> treeList = MenuHelper.buildTree(sysMenuList);
        return treeList;
    }

    @Override
    public void save(SysMenu sysMenu) {
        //解决一个小bug，角色里第一层菜单下面已经全部分配完后，再在前端添加第二层菜单后，在角色分配菜单回显里会把新增的勾上，但实际上并没有勾上，也不应该勾上
        SysMenu parent = sysMenuMapper.selectOne(new QueryWrapper<SysMenu>().eq("id", sysMenu.getParentId()));
        //TODO 依然有点小问题，虽然前端第二层新增菜单回显正常了，但是在第二层添加第三层菜单时，回显还是二层一下全选
        //TODO 但是这并不是后端原因，后端传入前端的数据里已经不包括is_half=1的父节点了，但是前端依然把父节点给勾上了
        //TODO 但是再添加一个二层菜单，三层菜单回显又正常了？？？？？？
        if(parent != null){
            UpdateWrapper updateWrapper = new UpdateWrapper<SysRoleMenu>().eq("menu_id", parent.getId());
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setIsHalf(1);
            sysRoleMenuMapper.update(sysRoleMenu,updateWrapper);
        }
        sysMenuMapper.insert(sysMenu);
    }

    @Override
    public void update(SysMenu sysMenu) {
        sysMenu.setUpdateTime(new Date());
        sysMenuMapper.updateById(sysMenu);
    }

    @Override
    public void removeById(Long id) {
        //根据当前菜单id，查询是否包含子菜单
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        if(sysMenuMapper.selectCount(queryWrapper)>0){
            throw new GuiguException(ResultCodeEnum.NODE_ERROR);
        }
        sysMenuMapper.deleteById(id);
    }

    @Override
    public List<SysMenuVo> findMenusByUserId() {
        //获取当前用户id
        SysUser sysUser = AuthContextUtil.get();
        Long userId = sysUser.getId();
        //根据userId查询可以操作菜单
        List<SysMenu> sysMenuList = sysMenuMapper.findMenusByUserId(userId);
        //封装成要求数据格式，返回
        List<SysMenu> sysMenuList1 = MenuHelper.buildTree(sysMenuList);
        List<SysMenuVo> sysMenuVos = this.buildMenus(sysMenuList1);
        return sysMenuVos;
    }

    // 将List<SysMenu>对象转换成List<SysMenuVo>对象
    private List<SysMenuVo> buildMenus(List<SysMenu> menus) {
        List<SysMenuVo> sysMenuVoList = new LinkedList<>();
        for (SysMenu sysMenu : menus) {
            SysMenuVo sysMenuVo = new SysMenuVo();
            sysMenuVo.setTitle(sysMenu.getTitle());
            sysMenuVo.setName(sysMenu.getComponent());
            List<SysMenu> children = sysMenu.getChildren();
            if (!CollectionUtils.isEmpty(children)) {
                sysMenuVo.setChildren(buildMenus(children));
            }
            sysMenuVoList.add(sysMenuVo);
        }
        return sysMenuVoList;
    }
}
