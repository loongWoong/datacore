package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建字段信息DTO")
public class CreateColumnDTO {
    
    @Schema(description = "字段名")
    private String columnName;
    
    @Schema(description = "中文字段名")
    private String columnNameCn;
    
    @Schema(description = "字段描述")
    private String columnDescription;
    
    @Schema(description = "数据类型")
    private String dataType;
    
    @Schema(description = "是否主键")
    private Integer isPrimaryKey;
    
    @Schema(description = "是否可为空")
    private Integer isNullable;
    
    @Schema(description = "字段顺序")
    private Integer columnOrder;
}