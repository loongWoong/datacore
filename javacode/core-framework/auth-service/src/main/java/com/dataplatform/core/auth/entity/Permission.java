package com.dataplatform.core.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
@Schema(description = "权限表")
public class Permission {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("permission_name")
    @Schema(description = "权限名称")
    private String permissionName;
    
    @TableField("permission_code")
    @Schema(description = "权限编码")
    private String permissionCode;
    
    @TableField("resource_type")
    @Schema(description = "资源类型：1-菜单，2-按钮，3-API接口")
    private Integer resourceType;
    
    @TableField("resource_url")
    @Schema(description = "资源URL")
    private String resourceUrl;
    
    @TableField("permission_desc")
    @Schema(description = "权限描述")
    private String permissionDesc;
    
    @TableField("status")
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}