package com.atguigu.spzx.model.entity.system;

import lombok.Data;

import java.util.List;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
@Data
public class SysRoleCodeUserId {
    private Long userId;
    private List<String> roleCodes;
}
