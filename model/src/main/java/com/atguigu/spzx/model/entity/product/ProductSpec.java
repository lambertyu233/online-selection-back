package com.atguigu.spzx.model.entity.product;

import com.atguigu.spzx.model.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商品规格实体类")
@TableName("product_spec")
public class ProductSpec extends BaseEntity {

	@Schema(description = "规格名称")
	private String specName;   // 规格名称

	@Schema(description = "规格值")
	private String specValue;  // 规格值

}