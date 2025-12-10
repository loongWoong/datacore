package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "任务日志查询条件DTO")
public class JobLogQuery {
    
    @Schema(description = "执行器主键ID")
    private Integer jobGroup;
    
    @Schema(description = "任务主键ID")
    private Integer jobId;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}