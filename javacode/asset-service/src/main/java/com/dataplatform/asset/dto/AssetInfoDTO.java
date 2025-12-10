package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "资产信息DTO")
public class AssetInfoDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "资产编码")
    private String assetCode;
    
    @Schema(description = "资产名称")
    private String assetName;
    
    @Schema(description = "资产中文名称")
    private String assetNameCn;
    
    @Schema(description = "资产描述")
    private String assetDescription;
    
    @Schema(description = "资产类型（TABLE/FIELD/API等）")
    private String assetType;
    
    @Schema(description = "业务域")
    private String businessDomain;
    
    @Schema(description = "数据源")
    private String dataSource;
    
    @Schema(description = "负责人")
    private String owner;
    
    @Schema(description = "所属部门")
    private String department;
    
    @Schema(description = "模式名称（表资产专用）")
    private String schemaName;
    
    @Schema(description = "表名（表资产专用）")
    private String tableName;
    
    @Schema(description = "字段名（字段资产专用）")
    private String columnName;
    
    @Schema(description = "敏感级别（PUBLIC/INTERNAL/CONFIDENTIAL/SECRET）")
    private String sensitivityLevel;
    
    @Schema(description = "资产状态（ACTIVE/DEPRECATED/ARCHIVED）")
    private String assetStatus;
    
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Schema(description = "更新时间")
    private Date updatedTime;
}