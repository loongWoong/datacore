package com.dataplatform.core.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "审计日志DTO")
public class AuditLogDto {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "操作描述")
    private String operation;
    
    @Schema(description = "方法名")
    private String method;
    
    @Schema(description = "请求参数")
    private String params;
    
    @Schema(description = "IP地址")
    private String ip;
    
    @Schema(description = "请求URI")
    private String uri;
    
    @Schema(description = "HTTP方法")
    private String httpMethod;
    
    @Schema(description = "用户代理")
    private String userAgent;
    
    @Schema(description = "执行时间（毫秒）")
    private Long executionTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}