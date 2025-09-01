package com.atnjupt.sqyxgo.order.reveiver;


import com.atnjupt.sqyxgo.mq.constant.MQConst;
import com.atnjupt.sqyxgo.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * ClassName: OrderReceiver
 * Package: com.atguigu.ssyx.order.reveiver
 * Description:
 *
 * @Author liang
 * @Create 2024/11/29 20:21
 * @Version jdk17.0
 */
@Component
@RequiredArgsConstructor
public class OrderReceiver {
    private final OrderInfoService orderInfoService;
    /**
     * 修改订单的支付状态，和删减库存
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_ORDER_PAY,durable = "true"),
           exchange = @Exchange(value = MQConst.EXCHANGE_PAY_DIRECT),
            key = {MQConst.ROUTING_PAY_SUCCESS}
    ))
    public void updateOrderInfoStatus(String orderNo, Message message , Channel channel) throws IOException {
      if(!StringUtils.isEmpty(orderNo)){
          orderInfoService. updateOrderInfoStatus(orderNo);
      }
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
