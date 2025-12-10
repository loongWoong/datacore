package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报表生成结果DTO")
public class ReportGenerationResultDTO {
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "生成记录ID")
    private Long recordId;
    
    @Schema(description = "文件名")
    private String fileName;
    
    @Schema(description = "文件大小")
    private Long fileSize;
}