package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "更新报表配置DTO")
public class UpdateReportConfigDTO {
    
    @Schema(description = "报表名称")
    private String reportName;
    
    @Schema(description = "报表描述")
    private String reportDescription;
    
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
    
    @Schema(description = "报表参数列表")
    private List<UpdateReportParameterDTO> parameters;
}