package com.wang.reggir.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.reggir.dto.OrdersDto;
import com.wang.reggir.pojo.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);

    public Page<OrdersDto> pageDto(int page, int pageSize);
}
