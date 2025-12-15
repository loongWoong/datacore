package com.dataplatform.datasource.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("datasource_info")
@Schema(description = "数据源信息表")
public class DatasourceInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("name")
    @Schema(description = "数据源名称")
    private String name;
    
    @TableField("type")
    @Schema(description = "数据源类型")
    private String type;
    
    @TableField("host")
    @Schema(description = "主机地址")
    private String host;
    
    @TableField("port")
    @Schema(description = "端口号")
    private Integer port;
    
    @TableField("database_name")
    @Schema(description = "数据库名称")
    private String databaseName;
    
    @TableField("username")
    @Schema(description = "用户名")
    private String username;
    
    @TableField("password")
    @Schema(description = "密码")
    private String password;
    
    @TableField("connection_url")
    @Schema(description = "连接URL")
    private String connectionUrl;
    
    @TableField("driver_class")
    @Schema(description = "驱动类")
    private String driverClass;
    
    @TableField("status")
    @Schema(description = "状态")
    private String status;
    
    @TableField("description")
    @Schema(description = "描述")
    private String description;
    
    @TableField("owner")
    @Schema(description = "负责人")
    private String owner;
    
    @TableField("environment")
    @Schema(description = "环境")
    private String environment;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}