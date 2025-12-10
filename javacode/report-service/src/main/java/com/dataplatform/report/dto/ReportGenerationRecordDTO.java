package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "报表生成记录DTO")
public class ReportGenerationRecordDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "报表编码")
    private String reportCode;
    
    @Schema(description = "报表名称")
    private String reportName;
    
    @Schema(description = "生成状态（PENDING/RUNNING/SUCCESS/FAILED）")
    private String generationStatus;
    
    @Schema(description = "开始时间")
    private Date startTime;
    
    @Schema(description = "结束时间")
    private Date endTime;
    
    @Schema(description = "执行耗时（毫秒）")
    private Long executionTime;
    
    @Schema(description = "生成文件路径")
    private String filePath;
    
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @Schema(description = "生成人")
    private String generatedBy;
    
    @Schema(description = "创建时间")
    private Date createdTime;
}