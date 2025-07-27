package com.atnjupt.sqyxgo.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:MQConfig
 * Package: com.atnjupt.sqyxgo.mq.config
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/20 15:52
 * @Version 1.0
 */

@Configuration
public class MQConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
