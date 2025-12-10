package com.dataplatform.lineage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "血缘节点DTO")
public class LineageNodeDTO {
    
    @Schema(description = "节点ID")
    private String id;
    
    @Schema(description = "节点标签")
    private String label;
    
    @Schema(description = "节点类型")
    private String type;
    
    @Schema(description = "节点属性")
    private Object properties;
}