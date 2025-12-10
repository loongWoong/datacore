package com.dataplatform.core.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user_role")
@Schema(description = "用户角色关联表")
public class UserRole {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;
    
    @TableField("role_id")
    @Schema(description = "角色ID")
    private Long roleId;
    
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}