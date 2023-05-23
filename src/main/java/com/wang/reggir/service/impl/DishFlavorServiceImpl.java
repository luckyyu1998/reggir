package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.mapper.DishFlavorMapper;
import com.wang.reggir.pojo.DishFlavor;
import com.wang.reggir.service.DishFlavorService;
import com.wang.reggir.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
