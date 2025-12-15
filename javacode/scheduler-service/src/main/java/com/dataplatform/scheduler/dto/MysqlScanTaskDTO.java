package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "MySQL扫描任务DTO")
public class MysqlScanTaskDTO {
    
    @Schema(description = "数据源ID")
    private Long datasourceId;
    
    @Schema(description = "输出文件路径")
    private String outputPath = "metadata_scan_result.yaml";
    
    @Schema(description = "任务执行超时时间，单位秒")
    private Integer executorTimeout = 300;
    
    @Schema(description = "失败重试次数")
    private Integer executorFailRetryCount = 0;
}