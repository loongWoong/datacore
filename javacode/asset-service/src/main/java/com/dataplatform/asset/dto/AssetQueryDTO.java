package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "资产查询条件DTO")
public class AssetQueryDTO {
    
    @Schema(description = "资产类型")
    private String assetType;
    
    @Schema(description = "业务域")
    private String businessDomain;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "搜索关键字")
    private String search;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}