package com.dataplatform.quality.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("quality_report")
@Schema(description = "质量报告表")
public class QualityReport {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("report_date")
    @Schema(description = "报告日期")
    private Date reportDate;
    
    @TableField("table_name")
    @Schema(description = "表名")
    private String tableName;
    
    @TableField("overall_score")
    @Schema(description = "综合质量得分")
    private BigDecimal overallScore;
    
    @TableField("completeness_rate")
    @Schema(description = "完整性率")
    private BigDecimal completenessRate;
    
    @TableField("accuracy_rate")
    @Schema(description = "准确率")
    private BigDecimal accuracyRate;
    
    @TableField("consistency_rate")
    @Schema(description = "一致率")
    private BigDecimal consistencyRate;
    
    @TableField("uniqueness_rate")
    @Schema(description = "唯一性率")
    private BigDecimal uniquenessRate;
    
    @TableField("timeliness_rate")
    @Schema(description = "及时性率")
    private BigDecimal timelinessRate;
    
    @TableField("total_records")
    @Schema(description = "总记录数")
    private Long totalRecords;
    
    @TableField("failed_rules")
    @Schema(description = "失败规则数")
    private Integer failedRules;
    
    @TableField("warning_rules")
    @Schema(description = "警告规则数")
    private Integer warningRules;
    
    @TableField("passed_rules")
    @Schema(description = "通过规则数")
    private Integer passedRules;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}