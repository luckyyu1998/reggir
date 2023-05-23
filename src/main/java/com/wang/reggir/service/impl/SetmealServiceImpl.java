package com.wang.reggir.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.common.CustomException;
import com.wang.reggir.dto.SetmealDto;
import com.wang.reggir.mapper.SetmealMapper;
import com.wang.reggir.pojo.Category;
import com.wang.reggir.pojo.Setmeal;
import com.wang.reggir.pojo.SetmealDish;
import com.wang.reggir.service.CategoryService;
import com.wang.reggir.service.SetmealDishService;
import com.wang.reggir.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        super.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public Page<SetmealDto> pageSetmealDto(int page, int pageSize, String name) {
        Page<Setmeal> pageinfo = new Page<>();
        Page<SetmealDto> pageDto = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name),Setmeal::getName,name)
                .orderByDesc(Setmeal::getUpdateTime);
        super.page(pageinfo, queryWrapper);
        BeanUtils.copyProperties(pageinfo, pageDto, "Records");
        List<Setmeal> records = pageinfo.getRecords();
        List<SetmealDto> setmealDtos = null;
        if (records != null) {
             setmealDtos= records.stream().map(setmeal -> {
                SetmealDto setmealDto = new SetmealDto();
                BeanUtils.copyProperties(setmeal, setmealDto);
                Category category = categoryService.getById(setmeal.getCategoryId());
                if (category != null) {
                    setmealDto.setCategoryName(category.getName());
                }
                return setmealDto;
            }).collect(Collectors.toList());
        }
        pageDto.setRecords(setmealDtos);
        return pageDto;
    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus,1);
        int count = super.count(setmealWrapper);
        if (count > 0){
            throw new CustomException("存在在售状态套餐，删除失败");
        }
        super.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(dishWrapper);
    }

    @Override
    public SetmealDto getDtoById(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = super.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;

    }

    @Override
    @Transactional
    public void updateWithDishes(SetmealDto setmealDto) {
        super.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list.forEach(item -> item.setSetmealId(setmealDto.getId()));
        setmealDishService.saveBatch(list);
    }
}
