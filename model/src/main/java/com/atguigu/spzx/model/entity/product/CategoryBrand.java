package com.atguigu.spzx.model.entity.product;

import com.atguigu.spzx.model.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分类品牌实体类")
@TableName("category_brand")
public class CategoryBrand extends BaseEntity {

	@Schema(description = "品牌id")
	private Long brandId;

	@Schema(description = "分类id")
	private Long categoryId;

	@Schema(description = "分类名称" , required = false)
	@TableField(exist = false)
	private String categoryName;

	@Schema(description = "品牌名称" , required = false)
	@TableField(exist = false)
	private String brandName;

	@Schema(description = "品牌logo" , required = false)
	@TableField(exist = false)
	private String logo;

}