package com.dataplatform.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新数据源DTO")
public class UpdateDatasourceDTO {
    
    @Schema(description = "数据源名称")
    private String name;
    
    @Schema(description = "数据源类型")
    private String type;
    
    @Schema(description = "主机地址")
    private String host;
    
    @Schema(description = "端口号")
    private Integer port;
    
    @Schema(description = "数据库名称")
    private String databaseName;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "驱动类")
    private String driverClass;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "环境")
    private String environment;
}