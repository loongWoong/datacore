package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "质量规则查询条件DTO")
public class QualityRuleQuery {
    
    @Schema(description = "规则类型")
    private String ruleType;
    
    @Schema(description = "表名")
    private String tableName;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}