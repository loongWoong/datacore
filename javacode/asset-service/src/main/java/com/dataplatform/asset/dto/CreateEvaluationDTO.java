package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "创建评估DTO")
public class CreateEvaluationDTO {
    
    @Schema(description = "资产ID")
    private Long assetId;
    
    @Schema(description = "资产等级（CORE/APPLICATION/BASIC）")
    private String assetLevel;
    
    @Schema(description = "使用频率（HIGH/MEDIUM/LOW）")
    private String usageFrequency;
    
    @Schema(description = "数据新鲜度（FRESH/STALE）")
    private String dataFreshness;
    
    @Schema(description = "业务价值评分（0-100）")
    private BigDecimal businessValue;
    
    @Schema(description = "技术质量评分（0-100）")
    private BigDecimal technicalQuality;
    
    @Schema(description = "安全等级评分（0-100）")
    private BigDecimal securityLevel;
    
    @Schema(description = "评估人")
    private String evaluator;
    
    @Schema(description = "评估备注")
    private String evaluationComment;
}