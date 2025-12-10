package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "表查询条件DTO")
public class TableQueryDTO {
    
    @Schema(description = "数据分层")
    private String layer;
    
    @Schema(description = "业务域")
    private String businessDomain;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "搜索关键字")
    private String search;
}