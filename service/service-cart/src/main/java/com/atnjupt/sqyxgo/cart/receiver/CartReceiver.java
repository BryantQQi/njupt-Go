package com.atguigu.ssyx.cart.receiver;

import com.atguigu.ssyx.cart.service.CartInfoService;
import com.atguigu.ssyx.mq.constant.MqConst;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ClassName: CartReceiver
 * Package: com.atguigu.ssyx.receiver
 * Description:
 *
 * @Author liang
 * @Create 2024/11/28 15:17
 * @Version jdk17.0
 */
@Component
@RequiredArgsConstructor
public class CartReceiver {
    private final CartInfoService cartInfoService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_DELETE_CART,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = {MqConst.ROUTING_DELETE_CART}
    ))
    public void deleteCart(Long userId, Message message , Channel channel) throws IOException {
        if (userId != null) {
            cartInfoService.deleteBuyCart(userId);

        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
