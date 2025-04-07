package com.atguigu.spzx.model.entity.base;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "产品单元实体类")
@TableName("product_unit")
public class ProductUnit extends BaseEntity {

	@Schema(description = "名称")
	private String name;

}