package com.dataplatform.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
    com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration.class,
    com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration.class,
    com.alibaba.cloud.nacos.NacosConfigAutoConfiguration.class
})
public class MetadataServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetadataServiceApplication.class, args);
    }
}