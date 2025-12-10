package com.dataplatform.lineage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "血缘关系DTO")
public class LineageRelationshipDTO {
    
    @Schema(description = "源表名")
    private String sourceTable;
    
    @Schema(description = "目标表名")
    private String targetTable;
    
    @Schema(description = "转换类型")
    private String transformType;
    
    @Schema(description = "描述")
    private String description;
}