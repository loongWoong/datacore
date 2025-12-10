package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "任务信息查询条件DTO")
public class JobInfoQuery {
    
    @Schema(description = "任务描述")
    private String jobDesc;
    
    @Schema(description = "执行器主键ID")
    private Integer jobGroup;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}