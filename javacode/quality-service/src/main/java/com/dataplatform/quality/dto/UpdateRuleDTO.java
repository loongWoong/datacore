package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "更新质量规则DTO")
public class UpdateRuleDTO {
    
    @Schema(description = "规则名称")
    private String ruleName;
    
    @Schema(description = "规则描述")
    private String ruleDescription;
    
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
}