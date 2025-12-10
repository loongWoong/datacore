package com.dataplatform.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("report_parameter")
@Schema(description = "报表参数表")
public class ReportParameter {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("report_id")
    @Schema(description = "报表ID")
    private Long reportId;
    
    @TableField("param_name")
    @Schema(description = "参数名称")
    private String paramName;
    
    @TableField("param_label")
    @Schema(description = "参数标签")
    private String paramLabel;
    
    @TableField("param_type")
    @Schema(description = "参数类型（STRING/INTEGER/DATE/BOOLEAN）")
    private String paramType;
    
    @TableField("is_required")
    @Schema(description = "是否必填")
    private Integer isRequired;
    
    @TableField("default_value")
    @Schema(description = "默认值")
    private String defaultValue;
    
    @TableField("param_options")
    @Schema(description = "参数选项（JSON格式，用于下拉框等）")
    private String paramOptions;
    
    @TableField("param_order")
    @Schema(description = "参数顺序")
    private Integer paramOrder;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}