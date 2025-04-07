package com.atguigu.spzx.manager.service.impl;

/**
 * @Author: lambertyu233
 * @Description:
 * @Version: 1.0
 */
import com.alibaba.excel.EasyExcel;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.listener.ExcelListener;
import com.atguigu.spzx.manager.mapper.CategoryMapper;
import com.atguigu.spzx.manager.service.CategoryService;
import com.atguigu.spzx.model.entity.product.Category;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.product.CategoryExcelVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> findCategoryList(Long id) {
        List<Category> categoryList = categoryMapper.selectList(new QueryWrapper<Category>().eq("parent_id", id).orderByAsc("order_num"));
        return categoryList;
    }

    @Override
    public void exportData(HttpServletResponse response) {
        try{
            //1 设置响应头信息和其他信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String filename = URLEncoder.encode("分类信息", "utf-8");
            //设置响应头信息 Content-disposition表示以下载的方式打开
            response.setHeader("Content-disposition","attachment;filename=" + filename + ".xlsx");
            //2 调用mapper方法查询所有分类，返回list集合
            List<Category> categoryList = categoryMapper.selectList(null);
            //List<Category>-------> List<CategoryExcelVo>
            List<CategoryExcelVo> categoryExcelVoList = new ArrayList<>();
            for(Category category : categoryList){
                CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
                //把Category的值获取出来，设置到CategoryExcelVo里面的快速方法
                BeanUtils.copyProperties(category, categoryExcelVo);
                categoryExcelVoList.add(categoryExcelVo);
            }
            //3 调用EasyExcel的write方法完成写操作
            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class).sheet("分类数据").doWrite(categoryExcelVoList);
        }catch (Exception e){
            e.printStackTrace();
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }

    @Override
    public void importData(MultipartFile file) {
        //监听器
        ExcelListener excelListener = new ExcelListener(categoryMapper);
        try {
            EasyExcel.read(file.getInputStream(),Category.class,excelListener).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //导入后把has_children更改了
        List<Long> noChildrenIds = categoryMapper.getNoChildrenIds();
        for(Long id : noChildrenIds){
            Category category = new Category();
            category.setId(id);
            category.setHasChildren(true);
            categoryMapper.updateById(category);
        }
    }
}
