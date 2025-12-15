package com.dataplatform.quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration.class,
    com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration.class,
    com.alibaba.cloud.nacos.NacosConfigAutoConfiguration.class
})
public class QualityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QualityServiceApplication.class, args);
    }
}