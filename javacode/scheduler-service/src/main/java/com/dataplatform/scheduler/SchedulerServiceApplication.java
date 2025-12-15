package com.dataplatform.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration.class,
    com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration.class,
    com.alibaba.cloud.nacos.NacosConfigAutoConfiguration.class
})
public class SchedulerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulerServiceApplication.class, args);
    }
}