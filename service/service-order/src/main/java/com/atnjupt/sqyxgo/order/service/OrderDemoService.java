package com.atnjupt.sqyxgo.order.service;

import com.atnjupt.sqyxgo.vo.order.OrderTimeoutDemoVo;

/**
 * 订单超时Demo服务接口
 */
public interface OrderDemoService {
    
    /**
     * 创建超时订单Demo
     * @param orderDemoVo 订单信息
     * @return 订单ID
     */
    Long createTimeoutOrder(OrderTimeoutDemoVo orderDemoVo);
    
    /**
     * 获取订单状态
     * @param orderNo 订单号
     * @return 订单状态描述
     */
    String getOrderStatus(String orderNo);
}
