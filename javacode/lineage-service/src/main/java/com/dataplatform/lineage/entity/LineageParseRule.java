package com.dataplatform.lineage.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("lineage_parse_rule")
@Schema(description = "血缘解析规则表")
public class LineageParseRule {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("rule_name")
    @Schema(description = "规则名称")
    private String ruleName;
    
    @TableField("rule_type")
    @Schema(description = "规则类型")
    private String ruleType;
    
    @TableField("source_pattern")
    @Schema(description = "源模式")
    private String sourcePattern;
    
    @TableField("target_pattern")
    @Schema(description = "目标模式")
    private String targetPattern;
    
    @TableField("transform_type")
    @Schema(description = "转换类型")
    private String transformType;
    
    @TableField("description")
    @Schema(description = "规则描述")
    private String description;
    
    @TableField("is_enabled")
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}