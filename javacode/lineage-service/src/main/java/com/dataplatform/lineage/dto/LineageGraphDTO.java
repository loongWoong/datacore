package com.dataplatform.lineage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "血缘图谱DTO")
public class LineageGraphDTO {
    
    @Schema(description = "节点列表")
    private List<LineageNodeDTO> nodes;
    
    @Schema(description = "边列表")
    private List<LineageEdgeDTO> edges;
}