package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "质量规则DTO")
public class QualityRuleDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "规则编码")
    private String ruleCode;
    
    @Schema(description = "规则名称")
    private String ruleName;
    
    @Schema(description = "规则描述")
    private String ruleDescription;
    
    @Schema(description = "规则类型（完整性、准确性、一致性、唯一性等）")
    private String ruleType;
    
    @Schema(description = "表名")
    private String tableName;
    
    @Schema(description = "字段名（适用于字段级规则）")
    private String columnName;
    
    @Schema(description = "检查表达式（SQL片段）")
    private String checkExpression;
    
    @Schema(description = "期望值")
    private String expectedValue;
    
    @Schema(description = "阈值")
    private BigDecimal threshold;
    
    @Schema(description = "严重级别（LOW/MEDIUM/HIGH）")
    private String severityLevel;
    
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @Schema(description = "创建人")
    private String creator;
    
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Schema(description = "更新时间")
    private Date updatedTime;
}