package com.atguigu.spzx.manager.utils;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.system.SysMenu;

import java.util.ArrayList;
import java.util.List;
//封装树形菜单数据
public class MenuHelper {

    //递归实现封装过程
    public static List<SysMenu> buildTree (List<SysMenu> sysMenuList) {
        //TODO 完成封装过程 看看能不能把代码改进一下
        //sysMenuList所有菜单集合
        //创建list集合，用于封装最终的数据
        List<SysMenu> trees = new ArrayList<>();
        //遍历所有菜单集合
        for(SysMenu sysMenu : sysMenuList) {
            //找到递归操作入口，第一层菜单
            //条件：parent_id=0
            if(sysMenu.getParentId() == 0) {
                //根据第一层，找下层数据，使用递归完成
                //写方法实现找下层过程，
                //方法里面传递两个参数：第一个参数是当前第一层菜单，第二个参数是所有菜单集合
                trees.add(findChildren(sysMenu,sysMenuList));
            }
        }
        return trees;
    }

    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        //sysMenu有属性 private List<SysMenu> children;封装下一层数据
        //先初始化
        sysMenu.setChildren(new ArrayList<>());
        //递归查询
        //sysMenu的id值 和 sysMenuList中parentId相同
        for(SysMenu sysMenuChild : sysMenuList) {
            //判断id和parentId是否相同
            //当子节点没有孩子节点的时候递归停止，因为if没有后续了
            if(sysMenuChild.getParentId() == sysMenu.getId()) {
                //sysMenuChild就是下层数据，进行封装，使用递归
                sysMenu.getChildren().add(findChildren(sysMenuChild,sysMenuList));
            }
        }
        return sysMenu;
    }
}
