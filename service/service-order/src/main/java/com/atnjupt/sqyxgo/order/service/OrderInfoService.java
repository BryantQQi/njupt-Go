package com.atguigu.ssyx.order.service;


import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.atguigu.ssyx.vo.order.OrderSubmitVo;
import com.atguigu.ssyx.vo.order.OrderUserQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author liang
* @description 针对表【order_info(订单)】的数据库操作Service
* @createDate 2024-11-26 15:31:06
*/
public interface OrderInfoService extends IService<OrderInfo> {

    OrderConfirmVo confirmOrder();

    Long submitOrder(OrderSubmitVo orderParamVo);

    OrderInfo getOrderInfoById(Long orderId);

    void updateOrderInfoStatus(String orderNo);

    IPage<OrderInfo> findUserOrderPage(Page<OrderInfo> pageParam, OrderUserQueryVo orderUserQueryVo);
}
