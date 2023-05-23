package com.wang.reggir.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.reggir.common.R;
import com.wang.reggir.pojo.Category;
import com.wang.reggir.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ResponseBody
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功！");
    }

    @ResponseBody
    @GetMapping("/page")
    public R<Page> pageshow(int page, int pageSize) {
        Page<Category> pageinfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getType)
                .orderByAsc(Category::getSort);
        categoryService.page(pageinfo, wrapper);
        return R.success(pageinfo);
    }

    @ResponseBody
    @DeleteMapping
    public R<String> delete(Long ids) {
//        categoryService.removeById(ids);
        categoryService.removeComplex(ids);
        return R.success("删除成功");
    }

    @ResponseBody
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @ResponseBody
    @GetMapping("/list")
    public R<List<Category>> listAllCategory(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
