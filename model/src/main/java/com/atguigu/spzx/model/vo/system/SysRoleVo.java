package com.atguigu.spzx.model.vo.system;

import com.atguigu.spzx.model.entity.system.SysRole;
import lombok.Data;

import java.util.List;

@Data
public class SysRoleVo {

    private List<SysRole> sysRoles;

    private Long pageTotal;
}
