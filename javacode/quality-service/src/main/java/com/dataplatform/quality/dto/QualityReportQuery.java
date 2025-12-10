package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "质量报告查询条件DTO")
public class QualityReportQuery {
    
    @Schema(description = "表名")
    private String tableName;
    
    @Schema(description = "开始日期")
    private LocalDate startDate;
    
    @Schema(description = "结束日期")
    private LocalDate endDate;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}