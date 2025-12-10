package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
@Schema(description = "生成报表DTO")
public class GenerateReportDTO {
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "报表参数")
    private Map<String, Object> parameters;
}