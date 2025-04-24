package com.atguigu.spzx.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "登录成功响应结果实体类")
public class LoginVo {

    @Schema(description = "令牌")
    private String token ;

    @Schema(description = "jwt")
    private String jwt ;

    @Schema(description = "刷新令牌,可以为空")
    private String refresh_token ;

    private List<String> authorities;
}
