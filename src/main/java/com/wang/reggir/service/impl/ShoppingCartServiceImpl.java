package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.mapper.ShoppingCartMapper;
import com.wang.reggir.pojo.ShoppingCart;
import com.wang.reggir.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public ShoppingCart saveComplextion(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null) {
            wrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                    .eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }
        if (shoppingCart.getSetmealId() != null) {
            wrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                    .eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartInData = super.getOne(wrapper);
        if (cartInData == null) {
            shoppingCart.setCreateTime(LocalDateTime.now());
            super.save(shoppingCart);
            return shoppingCart;
        }
        cartInData.setNumber(cartInData.getNumber() + 1);
        super.updateById(cartInData);
        return cartInData;
    }
}
