package com.atnjupt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ClassName:${NAME}
 * Package: com.atnjupt
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/9 9:59
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceGateWayApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceGateWayApplication.class,args);
    }
}