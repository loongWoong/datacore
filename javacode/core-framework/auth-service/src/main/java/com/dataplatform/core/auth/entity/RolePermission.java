package com.dataplatform.core.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
@Schema(description = "角色权限关联表")
public class RolePermission {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("role_id")
    @Schema(description = "角色ID")
    private Long roleId;
    
    @TableField("permission_id")
    @Schema(description = "权限ID")
    private Long permissionId;
    
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}