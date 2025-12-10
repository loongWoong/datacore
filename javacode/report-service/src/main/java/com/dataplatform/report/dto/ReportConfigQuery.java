package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "报表配置查询条件DTO")
public class ReportConfigQuery {
    
    @Schema(description = "报表类型")
    private String reportType;
    
    @Schema(description = "报表名称")
    private String reportName;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}