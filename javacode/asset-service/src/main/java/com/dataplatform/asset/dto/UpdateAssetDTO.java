package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新资产DTO")
public class UpdateAssetDTO {
    
    @Schema(description = "资产名称")
    private String assetName;
    
    @Schema(description = "资产中文名称")
    private String assetNameCn;
    
    @Schema(description = "资产描述")
    private String assetDescription;
    
    @Schema(description = "业务域")
    private String businessDomain;
    
    @Schema(description = "数据源")
    private String dataSource;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "所属部门")
    private String department;
    
    @Schema(description = "敏感级别（PUBLIC/INTERNAL/CONFIDENTIAL/SECRET）")
    private String sensitivityLevel;
    
    @Schema(description = "资产状态（ACTIVE/DEPRECATED/ARCHIVED）")
    private String assetStatus;
}