package com.atguigu.ssyx.order.service.impl;


import com.atguigu.ssyx.model.order.OrderItem;
import com.atguigu.ssyx.order.service.OrderItemService;
import com.atguigu.ssyx.order.mapper.OrderItemMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author liang
* @description 针对表【order_item(订单项信息)】的数据库操作Service实现
* @createDate 2024-11-26 15:31:06
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




