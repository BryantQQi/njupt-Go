package com.atnjupt.sqyxgo.mq.service;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * ClassName:RabbitService
 * Package: com.atnjupt.sqyxgo.mq
 * Description:
 * 主要是为了封装模版方法，提高代码复用性
 * 统一扩展点，便于后期增强
 * 延迟消息封装逻辑更清晰
 * 更贴合面向服务变成思想
 *
 * @Author Monkey
 * @Create 2025/7/20 15:19
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
@Api(tags = "mq发送消息接口测试")
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;

    //专门封装了一个发生消息的方法，更贴合业务，也可以不封装，在业务中引入RabbitTemplate进行发送
    public boolean sendMessage(String exchange,String routingKey,Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;

    }


}
