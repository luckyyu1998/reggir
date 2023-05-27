package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.common.CustomException;
import com.wang.reggir.dto.DishDto;
import com.wang.reggir.mapper.DishMapper;
import com.wang.reggir.pojo.Dish;
import com.wang.reggir.pojo.DishFlavor;
import com.wang.reggir.pojo.SetmealDish;
import com.wang.reggir.service.DishFlavorService;
import com.wang.reggir.service.DishService;
import com.wang.reggir.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void saveWithFlavors(DishDto dishDto) {
        super.save(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(i -> i.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(flavors);
        String key ="dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
    }

    @Override
    public DishDto getDishDtoByID(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = super.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavors(DishDto dishDto) {
        super.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(i -> i.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(flavors);
        String key ="dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
    }

    @Transactional
    @Override
    public void removeWithFlavors(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(Dish::getId,list).eq(Dish::getStatus,1);
        int count = super.count(dishWrapper);
        if (count > 0){
            throw new CustomException("存在在售状态菜品，删除失败");
        }

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,list);
        int count1 = setmealDishService.count(setmealDishLambdaQueryWrapper);
        if (count1 > 0){
            throw new CustomException("删除失败！存在与菜品关联的套餐，请先在套餐中取消与菜品的关联！");
        }

        super.removeByIds(list);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, list);
        dishFlavorService.remove(queryWrapper);

    }

    @Override
    @Transactional
    public void updateStatus(int sta, Long[] ids) {
        if(ids != null){
            List<Dish> dishList = Arrays.stream(ids).map(id -> {
                Dish dishupdate = super.getById(id);
                dishupdate.setStatus(sta);
                String key ="dish_" + dishupdate.getCategoryId();
                redisTemplate.delete(key);
                return dishupdate;
            }).collect(Collectors.toList());
            super.updateBatchById(dishList);


        }
    }

    @Override
    public List<DishDto> listWithFlavor(Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = super.list(queryWrapper);
        List<DishDto> dishDtos = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> flavorListlist = dishFlavorService.list(flavorLambdaQueryWrapper);
            dishDto.setFlavors(flavorListlist);
            return dishDto;
        }).collect(Collectors.toList());
        return dishDtos;
    }
}
