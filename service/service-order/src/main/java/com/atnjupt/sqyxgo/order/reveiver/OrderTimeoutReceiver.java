package com.atnjupt.sqyxgo.order.reveiver;

import com.atnjupt.sqyxgo.enums.OrderStatus;
import com.atnjupt.sqyxgo.model.order.OrderInfo;
import com.atnjupt.sqyxgo.mq.constant.MQConst;
import com.atnjupt.sqyxgo.order.mapper.OrderInfoMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;

/**
 * 订单超时消息接收器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderTimeoutReceiver {

    private final OrderInfoMapper orderInfoMapper;

    /**
     * 处理超时订单
     */
    @RabbitListener(queues = MQConst.QUEUE_ORDER_TIMEOUT_DLX)
    public void processTimeoutOrder(String orderNo, Message message, Channel channel) throws IOException {
        try {
            if (!StringUtils.hasText(orderNo)) {
                log.warn("接收到空的订单号消息");
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            log.info("开始处理超时订单：{}", orderNo);

            // 1. 查询订单信息
            LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderInfo::getOrderNo, orderNo);
            OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

            if (orderInfo == null) {
                log.warn("订单不存在：{}", orderNo);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 2. 检查订单支付状态
            if (orderInfo.getOrderStatus() == OrderStatus.UNPAID) {
                // 3. 订单未支付，设置为已取消
                LambdaUpdateWrapper<OrderInfo> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(OrderInfo::getOrderNo, orderNo)
                        .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID) // 防止并发问题
                        .set(OrderInfo::getOrderStatus, OrderStatus.CANCEL)
                        .set(OrderInfo::getCancelTime, new Date())
                        .set(OrderInfo::getCancelReason, "订单超时自动取消")
                        .set(OrderInfo::getUpdateTime, new Date());

                int updateCount = orderInfoMapper.update(null, updateWrapper);
                if (updateCount > 0) {
                    log.info("订单{}超时自动取消成功", orderNo);
                } else {
                    log.warn("订单{}状态更新失败，可能已被其他操作修改", orderNo);
                }
            } else {
                log.info("订单{}已支付，无需取消，当前状态：{}", orderNo, orderInfo.getOrderStatus().getComment());
            }

            // 4. 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error("处理超时订单异常，订单号：{}", orderNo, e);
            // 消息处理失败，拒绝消息但不重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
