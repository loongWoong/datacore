# Nacos Test Application

这是一个用于测试Nacos配置和注册中心功能的简单Spring Boot应用程序。

## 问题解决说明

原来的项目无法启动是因为缺少正确的Nacos配置。主要问题包括：
1. 缺少`bootstrap.yml`配置文件
2. 没有正确配置`spring.config.import`属性
3. 主应用程序类缺少`@EnableDiscoveryClient`注解

## 解决方案

我们已经做了以下修改：

1. 创建了`bootstrap.yml`文件，包含正确的Nacos配置：
   ```yaml
   spring:
     application:
       name: nacos-test
     cloud:
       nacos:
         config:
           server-addr: 192.168.22.212:8848
           file-extension: yaml
         discovery:
           server-addr: 192.168.22.212:8848
     config:
       import:
         - optional:nacos:nacos-test.yaml
   ```

2. 在主应用程序类上添加了`@EnableDiscoveryClient`注解

3. 简化了`application.yaml`文件，将Nacos相关配置移到Nacos服务器上

4. 添加了一个简单的测试控制器来验证应用程序是否正常工作

## 如何启动项目

1. 确保Nacos服务器正在运行，地址为：192.168.22.212:8848
2. 将`nacos-test.yaml`文件的内容上传到Nacos配置中心，Data ID为`nacos-test.yaml`
3. 使用Maven构建项目：
   ```
   mvn clean install
   ```
4. 启动应用程序：
   ```
   mvn spring-boot:run
   ```
5. 访问测试端点：http://localhost:8080/test

## 验证

如果一切正常，您应该能够看到返回消息"Nacos Test Application is running!"，并且该服务应该在Nacos控制台的服务列表中可见。