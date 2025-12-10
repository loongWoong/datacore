package com.dataplatform.quality.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("quality_check_result")
@Schema(description = "质量检查结果表")
public class QualityCheckResult {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("job_id")
    @Schema(description = "任务ID")
    private Long jobId;
    
    @TableField("rule_id")
    @Schema(description = "规则ID")
    private Long ruleId;
    
    @TableField("table_name")
    @Schema(description = "表名")
    private String tableName;
    
    @TableField("column_name")
    @Schema(description = "字段名")
    private String columnName;
    
    @TableField("check_date")
    @Schema(description = "检查日期")
    private Date checkDate;
    
    @TableField("check_status")
    @Schema(description = "检查状态（PASS/FAIL/WARNING）")
    private String checkStatus;
    
    @TableField("actual_value")
    @Schema(description = "实际值")
    private BigDecimal actualValue;
    
    @TableField("expected_value")
    @Schema(description = "期望值")
    private BigDecimal expectedValue;
    
    @TableField("deviation")
    @Schema(description = "偏差值")
    private BigDecimal deviation;
    
    @TableField("failed_records")
    @Schema(description = "失败记录详情（JSON格式）")
    private String failedRecords;
    
    @TableField("execution_time")
    @Schema(description = "执行耗时（毫秒）")
    private Long executionTime;
    
    @TableField("error_message")
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}