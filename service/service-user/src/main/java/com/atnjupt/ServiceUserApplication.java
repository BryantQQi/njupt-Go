package com.atnjupt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName:${NAME}
 * Package: com.atnjupt
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/21 22:47
 * @Version 1.0
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceUserApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceUserApplication.class,args);
    }
}