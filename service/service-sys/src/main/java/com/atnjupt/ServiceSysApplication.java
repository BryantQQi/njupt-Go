package com.atnjupt;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ClassName:${NAME}
 * Package: com.njupt
 * Description:
 *
 * @Author Monkey
 * @Create 2025/7/17 18:07
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceSysApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceSysApplication.class,args);
    }
}