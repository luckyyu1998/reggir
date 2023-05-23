package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.common.BaseContext;
import com.wang.reggir.common.CustomException;
import com.wang.reggir.dto.OrdersDto;
import com.wang.reggir.mapper.OrdersMapper;
import com.wang.reggir.pojo.*;
import com.wang.reggir.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        User user = userService.getById(orders.getUserId());
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,orders.getUserId());
        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        if (addressBook == null){
            throw new CustomException("未查询到地址信息");
        }
        if(user == null){
            throw new CustomException("未查询到用户信息");
        }
        if(list == null || list.size() < 1){
            throw new CustomException("购物车为空");
        }

        orders.setNumber(IdWorker.get32UUID());
        orders.setStatus(2);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        //如果用户name为空则用手机号代替用户名
        if(user.getName() != null){
            orders.setUserName(user.getName());
        }else {
            orders.setUserName(user.getPhone());
        }
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        //AtomicInteger保证多线程下的原子性
        AtomicInteger amount = new AtomicInteger(0);
        //BigDecimal amount = new BigDecimal(0);
        list.forEach(i ->{
            amount.addAndGet(i.getAmount().multiply(new BigDecimal(i.getNumber())).intValue());
//            amount.add(i.getAmount().multiply(new BigDecimal(i.getNumber())));
            });
        orders.setAmount(new BigDecimal(amount.get()));
        super.save(orders);

        List<OrderDetail> orderDetailList = list.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetailList);

        shoppingCartService.remove(wrapper);


    }

    @Override
    public Page<OrdersDto> pageDto(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> dtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId())
                                .orderByDesc(Orders::getOrderTime);
        super.page(ordersPage,ordersLambdaQueryWrapper);
        BeanUtils.copyProperties(ordersPage,dtoPage,"records");
        List<Orders> ordersPageRecords = ordersPage.getRecords();
        List<OrdersDto> dtoRecords = ordersPageRecords.stream().map(item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(wrapper);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoRecords);
        return dtoPage;
    }
}
