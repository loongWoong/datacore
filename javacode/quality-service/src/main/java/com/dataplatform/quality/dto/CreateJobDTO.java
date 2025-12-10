package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建检查任务DTO")
public class CreateJobDTO {
    
    @Schema(description = "任务名称")
    private String jobName;
    
    @Schema(description = "任务描述")
    private String jobDescription;
    
    @Schema(description = "关联的规则ID列表（逗号分隔）")
    private String ruleIds;
    
    @Schema(description = "Cron表达式")
    private String cronExpression;
    
    @Schema(description = "是否启用")
    private Integer isEnabled;
}