package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "高级搜索DTO")
public class AdvancedSearchDTO {
    
    @Schema(description = "资产类型")
    private String assetType;
    
    @Schema(description = "业务域")
    private String businessDomain;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "标签ID")
    private Long tagId;
    
    @Schema(description = "最小综合评分")
    private Double minScore;
    
    @Schema(description = "最大综合评分")
    private Double maxScore;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页大小")
    private Integer pageSize;
}