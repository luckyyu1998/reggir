package com.wang.reggir.dto;

import com.wang.reggir.pojo.OrderDetail;
import com.wang.reggir.pojo.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails;
	
}
