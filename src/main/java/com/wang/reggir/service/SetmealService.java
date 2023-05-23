package com.wang.reggir.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.reggir.dto.SetmealDto;
import com.wang.reggir.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public Page<SetmealDto> pageSetmealDto(int page, int pageSize, String name);

    public void deleteWithDish(List<Long> ids);

    public SetmealDto getDtoById(Long id);

    public void updateWithDishes(SetmealDto setmealDto);
}
