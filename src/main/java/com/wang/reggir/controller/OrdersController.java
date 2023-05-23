package com.wang.reggir.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.reggir.common.BaseContext;
import com.wang.reggir.common.R;
import com.wang.reggir.dto.OrdersDto;
import com.wang.reggir.pojo.Orders;
import com.wang.reggir.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @ResponseBody
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orders.setUserId(BaseContext.getCurrentId());
        ordersService.submit(orders);
        return R.success("添加订单成功");

    }

    @ResponseBody
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        Page<OrdersDto> pageinfo = ordersService.pageDto(page, pageSize);
        return R.success(pageinfo);
    }

    @ResponseBody
    @GetMapping("/page")
    public R<Page<Orders>> pageInBackend(int page, int pageSize, String number, String beginTime, String endTime) {
        //网页上传过来的时间信息直接用String接收即可
        //String字符串内容中的时间格式正确，可以直接用于wrapper中
        //数据库中对应字段是时间格式，实体类中对应属性是LocalDateTime类型
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(number), Orders::getNumber, number)
                .between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime)
                .orderByAsc(Orders::getStatus)
                .orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }

    @ResponseBody
    @PutMapping
    public R<String> updateStatue(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改订单状态成功");
    }
}
