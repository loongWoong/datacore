package com.dataplatform.core.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
@Schema(description = "用户表")
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("username")
    @Schema(description = "用户名")
    private String username;
    
    @TableField("password")
    @Schema(description = "密码")
    private String password;
    
    @TableField("nickname")
    @Schema(description = "昵称")
    private String nickname;
    
    @TableField("email")
    @Schema(description = "邮箱")
    private String email;
    
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;
    
    @TableField("status")
    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;
    
    @TableField("avatar")
    @Schema(description = "头像URL")
    private String avatar;
    
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}