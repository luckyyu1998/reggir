package com.wang.reggir.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.reggir.pojo.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    public ShoppingCart saveComplextion(ShoppingCart shoppingCart);
}
