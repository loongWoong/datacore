package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "评估趋势DTO")
public class EvaluationTrendDTO {
    
    @Schema(description = "评估日期")
    private Date evaluationDate;
    
    @Schema(description = "业务价值评分（0-100）")
    private BigDecimal businessValue;
    
    @Schema(description = "技术质量评分（0-100）")
    private BigDecimal technicalQuality;
    
    @Schema(description = "安全等级评分（0-100）")
    private BigDecimal securityLevel;
    
    @Schema(description = "综合评分（0-100）")
    private BigDecimal overallScore;
}