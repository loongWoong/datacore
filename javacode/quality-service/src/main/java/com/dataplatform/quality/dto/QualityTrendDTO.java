package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "质量趋势DTO")
public class QualityTrendDTO {
    
    @Schema(description = "报告日期")
    private Date reportDate;
    
    @Schema(description = "综合质量得分")
    private BigDecimal overallScore;
    
    @Schema(description = "完整性率")
    private BigDecimal completenessRate;
    
    @Schema(description = "准确率")
    private BigDecimal accuracyRate;
    
    @Schema(description = "一致率")
    private BigDecimal consistencyRate;
    
    @Schema(description = "唯一性率")
    private BigDecimal uniquenessRate;
    
    @Schema(description = "及时性率")
    private BigDecimal timelinessRate;
}