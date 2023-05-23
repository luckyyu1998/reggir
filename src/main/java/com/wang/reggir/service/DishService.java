package com.wang.reggir.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.reggir.dto.DishDto;
import com.wang.reggir.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavors(DishDto dishDto);
    public DishDto getDishDtoByID(Long id);

    public void updateWithFlavors(DishDto dishDto);

    public void removeWithFlavors(Long[] ids);

    public void updateStatus(int sta, Long[] ids);

    public List<DishDto> listWithFlavor(Long categoryId);
}
