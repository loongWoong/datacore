package com.dataplatform.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "数据源查询DTO")
public class DatasourceQueryDTO {
    
    @Schema(description = "数据源名称")
    private String name;
    
    @Schema(description = "数据源类型")
    private String type;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "环境")
    private String environment;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "搜索关键字")
    private String search;
}