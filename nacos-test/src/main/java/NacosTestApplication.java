package com.dataplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.dataplatform")
public class NacosTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosTestApplication.class, args);
    }
}