package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "生成记录查询条件DTO")
public class GenerationRecordQuery {
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "生成状态")
    private String generationStatus;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}