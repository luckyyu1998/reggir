package com.wang.reggir.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.reggir.common.R;
import com.wang.reggir.dto.DishDto;
import com.wang.reggir.pojo.Category;
import com.wang.reggir.pojo.Dish;
import com.wang.reggir.pojo.DishFlavor;
import com.wang.reggir.service.CategoryService;
import com.wang.reggir.service.DishFlavorService;
import com.wang.reggir.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    @ResponseBody
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavors(dishDto);
        return R.success("添加成功");
    }

    @ResponseBody
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);

    }

    @ResponseBody
    @GetMapping("{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishDtoByID(id);
        return R.success(dishDto);

    }

    @ResponseBody
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavors(dishDto);
        return R.success("更新成功");
    }

    @ResponseBody
    @DeleteMapping
    public R<String> delete(Long[] ids) {
        dishService.removeWithFlavors(ids);
        return R.success("删除成功");
    }

    @ResponseBody
    @PostMapping("/status/{sta}")
    public R<String> updateStatus(@PathVariable("sta") int sta, @RequestParam("ids") Long[] ids) {
        dishService.updateStatus(sta, ids);
        return R.success("更新状态成功");
    }

//    @ResponseBody
//    @GetMapping("/list")
//    public R<List<Dish>> getByCategoryId(Long categoryId) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId)
//                .eq(Dish::getStatus, 1)
//                .orderByAsc(Dish::getSort)
//                .orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishService.list(queryWrapper);
//        return R.success(dishList);
//    }

    @ResponseBody
    @GetMapping("/list")
    public R<List<DishDto>> getByCategoryId(Long categoryId) {
        String key = "dish_" + categoryId;
        List<DishDto> dishList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishList != null) {
            return R.success(dishList);
        }
        dishList = dishService.listWithFlavor(categoryId);
        redisTemplate.opsForValue().set(key, dishList, 60, TimeUnit.MINUTES);
        return R.success(dishList);
    }


}
