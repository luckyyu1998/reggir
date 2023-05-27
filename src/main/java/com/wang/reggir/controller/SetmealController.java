package com.wang.reggir.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.reggir.common.R;
import com.wang.reggir.dto.SetmealDto;
import com.wang.reggir.pojo.Setmeal;
import com.wang.reggir.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @ResponseBody
    @PostMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId")
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("套餐添加成功");
    }

    @ResponseBody
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDto> setmealDtoPage = setmealService.pageSetmealDto(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    @ResponseBody
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        //对于接受多个数据，如果用数组则无需指定@RequestParam，如果用list接受则必须用注解@RequestParam
        setmealService.deleteWithDish(ids);
        return R.success("套餐删除成功");
    }

    @ResponseBody
    @PostMapping("/status/{sta}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> updateStatus(@PathVariable("sta") int sta, @RequestParam List<Long> ids) {
        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Setmeal::getStatus, sta);
        wrapper.in(Setmeal::getId, ids);
        setmealService.update(wrapper);
        return R.success("更新状态成功");

    }

    @ResponseBody
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getDtoById(id);
        return R.success(setmealDto);

    }

    @ResponseBody
    @PutMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId")
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDishes(setmealDto);
        return R.success("修改成功");
    }

    @ResponseBody
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#categoryId")
    public R<List<Setmeal>> list(Long categoryId) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId, categoryId)
                .eq(Setmeal::getStatus, 1)
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(wrapper);
        return R.success(setmeals);
    }

}
