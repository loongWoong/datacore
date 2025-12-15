package com.dataplatform.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 生成请求ID用于跟踪
        String requestId = UUID.randomUUID().toString();
        
        ServerHttpRequest request = exchange.getRequest();
        
        // 记录请求信息
        log.info("[{}] 收到请求: {} {}", requestId, request.getMethod(), request.getURI());
        log.debug("[{}] 请求头信息:", requestId);
        request.getHeaders().forEach((name, values) -> 
            values.forEach(value -> log.debug("[{}]   {}: {}", requestId, name, value))
        );
        
        // 继续处理请求并记录响应信息
        return chain.filter(exchange).then(
            Mono.fromRunnable(() -> {
                ServerHttpResponse response = exchange.getResponse();
                log.info("[{}] 响应状态码: {}", requestId, response.getStatusCode());
            })
        );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}