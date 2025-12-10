package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "报表配置详情DTO")
public class ReportConfigDetailDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "报表编码")
    private String reportCode;
    
    @Schema(description = "报表名称")
    private String reportName;
    
    @Schema(description = "报表描述")
    private String reportDescription;
    
    @Schema(description = "报表类型（SIMPLE/TEMPLATE/DASHBOARD）")
    private String reportType;
    
    @Schema(description = "数据源类型（SQL/API）")
    private String dataSourceType;
    
    @Schema(description = "数据源配置（JSON格式）")
    private String dataSourceConfig;
    
    @Schema(description = "模板文件路径")
    private String templateFilePath;
    
    @Schema(description = "输出格式（EXCEL/PDF/HTML/CSV）")
    private String outputFormat;
    
    @Schema(description = "调度配置（JSON格式）")
    private String scheduleConfig;
    
    @Schema(description = "是否启用")
    private Integer isEnabled;
    
    @Schema(description = "创建人")
    private String creator;
    
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Schema(description = "更新时间")
    private Date updatedTime;
    
    @Schema(description = "报表参数列表")
    private List<ReportParameterDTO> parameters;
}