package com.atnjupt.sqyxgo.search.receiver;

import com.atnjupt.sqyxgo.mq.constant.MQConst;
import com.atnjupt.sqyxgo.search.service.SkuService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ClassName:SkuReceiver
 * Package: com.atnjupt.sqyxgo.search.receiver
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 16:49
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SkuReceiver {

    private final SkuService skuService;

    //商品上架消息的监听
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_GOODS_UPPER,durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_GOODS_DIRECT),
            key = {MQConst.ROUTING_GOODS_UPPER} ))
    public void upperSku(Long skuId, Message message, Channel channel) throws IOException {
        log.info("消费 skuId: {}", skuId);
        if (skuId != null) {
            //调用方法进行消费
            skuService.upperSkuById(skuId);
        }
        //手动ack确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    //商品下架消息的监听
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_GOODS_LOWER,durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_GOODS_DIRECT),
            key = {MQConst.ROUTING_GOODS_LOWER} ))
    public void downSku(Long skuId, Message message, Channel channel) throws IOException {
        if (skuId != null) {
            //调用方法进行消费
            skuService.downSkuInfoById(skuId);
        }
        //手动ack确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }



}
