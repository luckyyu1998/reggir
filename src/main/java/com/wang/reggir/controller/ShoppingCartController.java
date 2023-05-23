package com.wang.reggir.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wang.reggir.common.BaseContext;
import com.wang.reggir.common.R;
import com.wang.reggir.pojo.ShoppingCart;
import com.wang.reggir.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @ResponseBody
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId())
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);
        return R.success(shoppingCartList);

    }

    @ResponseBody
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //添加用户ID
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart finalCart = shoppingCartService.saveComplextion(shoppingCart);
        return R.success(finalCart);
    }

    @ResponseBody
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success("清空成功");
    }
    @ResponseBody
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        if (shoppingCart.getSetmealId() != null){
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }else {
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }

        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        if (cart != null){
            if (cart.getNumber() > 1){
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartService.updateById(cart);
            }else {
                shoppingCartService.removeById(cart.getId());
            }
        }
        return R.success("修改成功");

    }

}
