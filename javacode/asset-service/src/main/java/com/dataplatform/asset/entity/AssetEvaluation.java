package com.dataplatform.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("asset_evaluation")
@Schema(description = "资产评估指标表")
public class AssetEvaluation {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("asset_id")
    @Schema(description = "资产ID")
    private Long assetId;
    
    @TableField("evaluation_date")
    @Schema(description = "评估日期")
    private Date evaluationDate;
    
    @TableField("asset_level")
    @Schema(description = "资产等级（CORE/APPLICATION/BASIC）")
    private String assetLevel;
    
    @TableField("usage_frequency")
    @Schema(description = "使用频率（HIGH/MEDIUM/LOW）")
    private String usageFrequency;
    
    @TableField("data_freshness")
    @Schema(description = "数据新鲜度（FRESH/STALE）")
    private String dataFreshness;
    
    @TableField("business_value")
    @Schema(description = "业务价值评分（0-100）")
    private BigDecimal businessValue;
    
    @TableField("technical_quality")
    @Schema(description = "技术质量评分（0-100）")
    private BigDecimal technicalQuality;
    
    @TableField("security_level")
    @Schema(description = "安全等级评分（0-100）")
    private BigDecimal securityLevel;
    
    @TableField("overall_score")
    @Schema(description = "综合评分（0-100）")
    private BigDecimal overallScore;
    
    @TableField("evaluator")
    @Schema(description = "评估人")
    private String evaluator;
    
    @TableField("evaluation_comment")
    @Schema(description = "评估备注")
    private String evaluationComment;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}