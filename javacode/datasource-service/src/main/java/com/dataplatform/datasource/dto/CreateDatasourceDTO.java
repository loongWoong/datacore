package com.dataplatform.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建数据源DTO")
public class CreateDatasourceDTO {
    
    @Schema(description = "数据源名称", required = true)
    private String name;
    
    @Schema(description = "数据源类型", required = true)
    private String type;
    
    @Schema(description = "主机地址", required = true)
    private String host;
    
    @Schema(description = "端口号", required = true)
    private Integer port;
    
    @Schema(description = "数据库名称", required = true)
    private String databaseName;
    
    @Schema(description = "用户名", required = true)
    private String username;
    
    @Schema(description = "密码", required = true)
    private String password;
    
    @Schema(description = "驱动类")
    private String driverClass;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "环境")
    private String environment;
}