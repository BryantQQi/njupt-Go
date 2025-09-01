package com.atnjupt.sqyxgo.order.service;


import com.atnjupt.sqyxgo.model.order.OrderInfo;
import com.atnjupt.sqyxgo.vo.order.OrderConfirmVo;
import com.atnjupt.sqyxgo.vo.order.OrderSubmitVo;
import com.atnjupt.sqyxgo.vo.order.OrderUserQueryVo;
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
