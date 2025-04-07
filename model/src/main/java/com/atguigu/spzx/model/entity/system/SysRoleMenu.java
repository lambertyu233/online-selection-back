package com.atguigu.spzx.model.entity.system;


import com.atguigu.spzx.model.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {
    private Long roleId;       // 角色id
    private Long menuId;       // 菜单id
    private Integer isHalf;
}
