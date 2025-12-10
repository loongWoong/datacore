package com.dataplatform.lineage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("lineage_config")
@Schema(description = "血缘配置表")
public class LineageConfig {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("config_key")
    @Schema(description = "配置键")
    private String configKey;
    
    @TableField("config_value")
    @Schema(description = "配置值")
    private String configValue;
    
    @TableField("config_type")
    @Schema(description = "配置类型")
    private String configType;
    
    @TableField("description")
    @Schema(description = "配置描述")
    private String description;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}