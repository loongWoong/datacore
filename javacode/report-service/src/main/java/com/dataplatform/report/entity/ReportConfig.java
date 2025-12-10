package com.dataplatform.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("report_config")
@Schema(description = "报表配置表")
public class ReportConfig {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("report_code")
    @Schema(description = "报表编码")
    private String reportCode;
    
    @TableField("report_name")
    @Schema(description = "报表名称")
    private String reportName;
    
    @TableField("report_description")
    @Schema(description = "报表描述")
    private String reportDescription;
    
    @TableField("report_type")
    @Schema(description = "报表类型（SIMPLE/TEMPLATE/DASHBOARD）")
    private String reportType;
    
    @TableField("data_source_type")
    @Schema(description = "数据源类型（SQL/API）")
    private String dataSourceType;
    
    @TableField("data_source_config")
    @Schema(description = "数据源配置（JSON格式）")
    private String dataSourceConfig;
    
    @TableField("template_file_path")
    @Schema(description = "模板文件路径")
    private String templateFilePath;
    
    @TableField("output_format")
    @Schema(description = "输出格式（EXCEL/PDF/HTML/CSV）")
    private String outputFormat;
    
    @TableField("schedule_config")
    @Schema(description = "调度配置（JSON格式）")
    private String scheduleConfig;
    
    @TableField("is_enabled")
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @TableField("creator")
    @Schema(description = "创建人")
    private String creator;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}