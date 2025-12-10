package com.dataplatform.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("xxl_job_registry")
@Schema(description = "执行器注册表")
public class JobRegistry {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @TableField("registry_group")
    @Schema(description = "注册组")
    private String registryGroup;
    
    @TableField("registry_key")
    @Schema(description = "注册键")
    private String registryKey;
    
    @TableField("registry_value")
    @Schema(description = "注册值")
    private String registryValue;
    
    @TableField("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;
}