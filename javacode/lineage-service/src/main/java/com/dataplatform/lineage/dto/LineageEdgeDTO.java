package com.dataplatform.lineage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "血缘边DTO")
public class LineageEdgeDTO {
    
    @Schema(description = "源节点ID")
    private String source;
    
    @Schema(description = "目标节点ID")
    private String target;
    
    @Schema(description = "边类型")
    private String type;
    
    @Schema(description = "边属性")
    private Object properties;
}