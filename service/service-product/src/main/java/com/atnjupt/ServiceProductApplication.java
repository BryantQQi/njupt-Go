package com.atnjupt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import lombok.extern.slf4j.Slf4j;
/**
 * ClassName:${NAME}
 * Package: com.atnjupt
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/18 15:36
 * @Version 1.0
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class,args);
        log.info("service-product启动成功");
    }
}