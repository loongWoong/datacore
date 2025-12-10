package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "质量检查任务DTO")
public class QualityCheckJobDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
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
    
    @Schema(description = "最后执行时间")
    private Date lastExecuteTime;
    
    @Schema(description = "下次执行时间")
    private Date nextExecuteTime;
    
    @Schema(description = "创建人")
    private String creator;
    
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Schema(description = "更新时间")
    private Date updatedTime;
}