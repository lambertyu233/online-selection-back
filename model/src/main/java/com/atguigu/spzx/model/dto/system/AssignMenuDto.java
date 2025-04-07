package com.atguigu.spzx.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "请求参数实体类")
public class AssignMenuDto {

    @Schema(description = "角色id")
    private Long roleId;			// 角色id

    @Schema(description = "选中的菜单id的集合")
    private List<Map<String , Number>> menuIdList;	// 选中的菜单id的集合; Map的键表示菜单的id，值表示是否为半开; 0否，1是

}

/*
前端返回数据示例：
{roleId: 2, menuIdList: Array(4)}
其中menuIdList
menuIdList: Array(4)
[
0: {id: 2, isHalf: 0}
1: {id: 3, isHalf: 0}
2: {id: 4, isHalf: 0}
3: {id: 1, isHalf: 1}    此为父节点
]
* */