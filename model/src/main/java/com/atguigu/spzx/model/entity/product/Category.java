package com.atguigu.spzx.model.entity.product;

import com.alibaba.excel.annotation.ExcelProperty;
import com.atguigu.spzx.model.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分类实体类")
@TableName("category")
public class Category extends BaseEntity {

	@Schema(description = "分类名称")
	@ExcelProperty(value = "名称" ,index = 1)
	private String name;

	@Schema(description = "分类图片url")
	@ExcelProperty(value = "图片url" ,index = 2)
	private String imageUrl;

	@Schema(description = "父节点id")
	@ExcelProperty(value = "上级id" ,index = 3)
	private Long parentId;

	@Schema(description = "分类状态: 是否显示[0-不显示，1显示]")
	@ExcelProperty(value = "状态" ,index = 4)
	private Integer status;

	@Schema(description = "排序字段")
	@ExcelProperty(value = "排序" ,index = 5)
	private Integer orderNum;

	@Schema(description = "是否存在子节点")
	private Boolean hasChildren;

	@Schema(description = "子节点List集合")
	@TableField(exist = false)
	private List<Category> children;

}