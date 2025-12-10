package com.dataplatform.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "报表参数DTO")
public class ReportParameterDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "报表ID")
    private Long reportId;
    
    @Schema(description = "参数名称")
    private String paramName;
    
    @Schema(description = "参数标签")
    private String paramLabel;
    
    @Schema(description = "参数类型（STRING/INTEGER/DATE/BOOLEAN）")
    private String paramType;
    
    @Schema(description = "是否必填")
    private Integer isRequired;
    
    @Schema(description = "默认值")
    private String defaultValue;
    
    @Schema(description = "参数选项（JSON格式，用于下拉框等）")
    private String paramOptions;
    
    @Schema(description = "参数顺序")
    private Integer paramOrder;
    
    @Schema(description = "创建时间")
    private Date createdTime;
}