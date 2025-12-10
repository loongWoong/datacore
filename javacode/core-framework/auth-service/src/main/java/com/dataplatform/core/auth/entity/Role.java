package com.dataplatform.core.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
@Schema(description = "角色表")
public class Role {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("role_name")
    @Schema(description = "角色名称")
    private String roleName;
    
    @TableField("role_code")
    @Schema(description = "角色编码")
    private String roleCode;
    
    @TableField("description")
    @Schema(description = "角色描述")
    private String description;
    
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