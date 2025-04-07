package com.atguigu.spzx.model.vo.system;

import com.atguigu.spzx.model.entity.system.SysUser;
import lombok.Data;
import java.util.List;

@Data
public class SysUserVo {

    private List<SysUser> sysUsers;

    private Long pageTotal;
}
