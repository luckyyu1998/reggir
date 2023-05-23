package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.common.CustomException;
import com.wang.reggir.mapper.CategoryMapper;
import com.wang.reggir.pojo.Category;
import com.wang.reggir.pojo.Dish;
import com.wang.reggir.pojo.Setmeal;
import com.wang.reggir.service.CategoryService;
import com.wang.reggir.service.DishService;
import com.wang.reggir.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public void removeComplex(Long id) {
        //查询分类是否关联菜品，如果已关联则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount > 0 ){
            throw new CustomException("已关联菜品无法删除");
        }
        //查询分类是否关联套餐，如果已关联则抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount > 0){
            throw new CustomException("已关联套餐无法删除");
        }

        //均未关联则删除
        categoryMapper.deleteById(id);
    }
}
