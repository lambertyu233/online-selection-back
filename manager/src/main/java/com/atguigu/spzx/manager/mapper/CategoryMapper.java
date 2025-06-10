package com.atguigu.spzx.manager.mapper;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.atguigu.spzx.model.entity.product.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    //查询所有有子菜单的菜单id
    @Select("select id from category where id in (select parent_id from category) and has_children =0")
    List<Long> getNoChildrenIds();
}
