package com.dataplatform.quality.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("quality_check_job")
@Schema(description = "质量检查任务表")
public class QualityCheckJob {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("job_name")
    @Schema(description = "任务名称")
    private String jobName;
    
    @TableField("job_description")
    @Schema(description = "任务描述")
    private String jobDescription;
    
    @TableField("rule_ids")
    @Schema(description = "关联的规则ID列表（逗号分隔）")
    private String ruleIds;
    
    @TableField("cron_expression")
    @Schema(description = "Cron表达式")
    private String cronExpression;
    
    @TableField("is_enabled")
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @TableField("last_execute_time")
    @Schema(description = "最后执行时间")
    private Date lastExecuteTime;
    
    @TableField("next_execute_time")
    @Schema(description = "下次执行时间")
    private Date nextExecuteTime;
    
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