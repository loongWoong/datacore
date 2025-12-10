package com.dataplatform.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("report_generation_record")
@Schema(description = "报表生成记录表")
public class ReportGenerationRecord {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("report_id")
    @Schema(description = "报表ID")
    private Long reportId;
    
    @TableField("report_code")
    @Schema(description = "报表编码")
    private String reportCode;
    
    @TableField("report_name")
    @Schema(description = "报表名称")
    private String reportName;
    
    @TableField("generation_status")
    @Schema(description = "生成状态（PENDING/RUNNING/SUCCESS/FAILED）")
    private String generationStatus;
    
    @TableField("start_time")
    @Schema(description = "开始时间")
    private Date startTime;
    
    @TableField("end_time")
    @Schema(description = "结束时间")
    private Date endTime;
    
    @TableField("execution_time")
    @Schema(description = "执行耗时（毫秒）")
    private Long executionTime;
    
    @TableField("file_path")
    @Schema(description = "生成文件路径")
    private String filePath;
    
    @TableField("file_size")
    @Schema(description = "文件大小（字节）")
    private Long fileSize;
    
    @TableField("error_message")
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @TableField("generated_by")
    @Schema(description = "生成人")
    private String generatedBy;
    
    @TableField("generated_params")
    @Schema(description = "生成参数（JSON格式）")
    private String generatedParams;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}