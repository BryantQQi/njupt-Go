package com.atnjupt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName:${NAME}
 * Package: com.atnjupt
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/21 22:48
 * @Version 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceHomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHomeApplication.class,args);
    }
}