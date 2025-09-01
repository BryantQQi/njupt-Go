package com.atnjupt.sqyxgo.order.service.impl;

import com.atnjupt.sqyxgo.enums.OrderStatus;
import com.atnjupt.sqyxgo.enums.ProcessStatus;
import com.atnjupt.sqyxgo.model.order.OrderInfo;
import com.atnjupt.sqyxgo.mq.constant.MQConst;
import com.atnjupt.sqyxgo.mq.service.RabbitService;
import com.atnjupt.sqyxgo.order.mapper.OrderInfoMapper;
import com.atnjupt.sqyxgo.order.service.OrderDemoService;
import com.atnjupt.sqyxgo.vo.order.OrderTimeoutDemoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单超时Demo服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDemoServiceImpl implements OrderDemoService {

    private final OrderInfoMapper orderInfoMapper;
    private final RabbitService rabbitService;

    @Override
    @Transactional
    public Long createTimeoutOrder(OrderTimeoutDemoVo orderDemoVo) {
        // 1. 创建订单
        OrderInfo orderInfo = new OrderInfo();
        String orderNo = "DEMO" + System.currentTimeMillis();
        
        orderInfo.setOrderNo(orderNo);
        orderInfo.setUserId(1L); // Demo用户ID
        orderInfo.setNickName(orderDemoVo.getNickName());
        orderInfo.setTotalAmount(orderDemoVo.getTotalAmount());
        orderInfo.setOriginalTotalAmount(orderDemoVo.getTotalAmount());
        orderInfo.setActivityAmount(BigDecimal.ZERO);
        orderInfo.setCouponAmount(BigDecimal.ZERO);
        orderInfo.setFeightFee(BigDecimal.ZERO);
        orderInfo.setFeightFeeReduce(BigDecimal.ZERO);
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.setRemark(orderDemoVo.getRemark());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        
        // 2. 插入数据库
        orderInfoMapper.insert(orderInfo);
        log.info("创建订单成功，订单号：{}", orderNo);
        
        // 3. 发送延迟消息到死信队列
        rabbitService.sendMessage(
            MQConst.EXCHANGE_ORDER_TIMEOUT_DIRECT,
            MQConst.ROUTING_ORDER_CREATE,
            orderNo
        );
        log.info("发送订单超时检查消息，订单号：{}", orderNo);
        
        return orderInfo.getId();
    }

    @Override
    public String getOrderStatus(String orderNo) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
        
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        if (orderInfo == null) {
            return "订单不存在";
        }
        
        return orderInfo.getOrderStatus().getComment();
    }
}
