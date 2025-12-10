package com.dataplatform.quality.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("quality_rule")
@Schema(description = "数据质量规则表")
public class QualityRule {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("rule_code")
    @Schema(description = "规则编码")
    private String ruleCode;
    
    @TableField("rule_name")
    @Schema(description = "规则名称")
    private String ruleName;
    
    @TableField("rule_description")
    @Schema(description = "规则描述")
    private String ruleDescription;
    
    @TableField("rule_type")
    @Schema(description = "规则类型（完整性、准确性、一致性、唯一性等）")
    private String ruleType;
    
    @TableField("table_name")
    @Schema(description = "表名")
    private String tableName;
    
    @TableField("column_name")
    @Schema(description = "字段名（适用于字段级规则）")
    private String columnName;
    
    @TableField("check_expression")
    @Schema(description = "检查表达式（SQL片段）")
    private String checkExpression;
    
    @TableField("expected_value")
    @Schema(description = "期望值")
    private String expectedValue;
    
    @TableField("threshold")
    @Schema(description = "阈值")
    private BigDecimal threshold;
    
    @TableField("severity_level")
    @Schema(description = "严重级别（LOW/MEDIUM/HIGH）")
    private String severityLevel;
    
    @TableField("is_enabled")
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @TableField("creator")
    @Schema(description = "创建人")
    private String creator;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}