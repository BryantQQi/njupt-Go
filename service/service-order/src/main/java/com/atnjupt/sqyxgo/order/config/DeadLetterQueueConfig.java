package com.atnjupt.sqyxgo.order.config;

import com.atnjupt.sqyxgo.mq.constant.MQConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单超时关单死信队列配置
 */
@Configuration
public class DeadLetterQueueConfig {

    /**
     * 订单超时延迟交换机
     */
    @Bean
    public DirectExchange orderTimeoutExchange() {
        return new DirectExchange(MQConst.EXCHANGE_ORDER_TIMEOUT_DIRECT, true, false);
    }

    /**
     * 订单超时死信交换机
     */
    @Bean
    public DirectExchange orderTimeoutDlxExchange() {
        return new DirectExchange(MQConst.EXCHANGE_ORDER_TIMEOUT_DLX, true, false);
    }

    /**
     * 订单超时延迟队列（设置TTL和死信交换机）
     */
    @Bean
    public Queue orderTimeoutDelayQueue() {
        return QueueBuilder.durable(MQConst.QUEUE_ORDER_TIMEOUT_DELAY)
                .withArgument("x-message-ttl", 20000) // 20秒TTL
                .withArgument("x-dead-letter-exchange", MQConst.EXCHANGE_ORDER_TIMEOUT_DLX)
                .withArgument("x-dead-letter-routing-key", MQConst.ROUTING_ORDER_TIMEOUT)
                .build();
    }

    /**
     * 订单超时死信队列
     */
    @Bean
    public Queue orderTimeoutDlxQueue() {
        return new Queue(MQConst.QUEUE_ORDER_TIMEOUT_DLX, true);
    }

    /**
     * 延迟队列绑定
     */
    @Bean
    public Binding orderTimeoutDelayBinding() {
        return BindingBuilder.bind(orderTimeoutDelayQueue())
                .to(orderTimeoutExchange())
                .with(MQConst.ROUTING_ORDER_CREATE);
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding orderTimeoutDlxBinding() {
        return BindingBuilder.bind(orderTimeoutDlxQueue())
                .to(orderTimeoutDlxExchange())
                .with(MQConst.ROUTING_ORDER_TIMEOUT);
    }
}
