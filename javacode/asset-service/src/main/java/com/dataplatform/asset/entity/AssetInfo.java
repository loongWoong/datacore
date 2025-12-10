package com.dataplatform.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("asset_info")
@Schema(description = "数据资产信息表")
public class AssetInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("asset_code")
    @Schema(description = "资产编码")
    private String assetCode;
    
    @TableField("asset_name")
    @Schema(description = "资产名称")
    private String assetName;
    
    @TableField("asset_name_cn")
    @Schema(description = "资产中文名称")
    private String assetNameCn;
    
    @TableField("asset_description")
    @Schema(description = "资产描述")
    private String assetDescription;
    
    @TableField("asset_type")
    @Schema(description = "资产类型（TABLE/FIELD/API等）")
    private String assetType;
    
    @TableField("business_domain")
    @Schema(description = "业务域")
    private String businessDomain;
    
    @TableField("data_source")
    @Schema(description = "数据源")
    private String dataSource;
    
    @TableField("owner")
    @Schema(description = "负责人")
    private String owner;
    
    @TableField("department")
    @Schema(description = "所属部门")
    private String department;
    
    @TableField("schema_name")
    @Schema(description = "模式名称（表资产专用）")
    private String schemaName;
    
    @TableField("table_name")
    @Schema(description = "表名（表资产专用）")
    private String tableName;
    
    @TableField("column_name")
    @Schema(description = "字段名（字段资产专用）")
    private String columnName;
    
    @TableField("sensitivity_level")
    @Schema(description = "敏感级别（PUBLIC/INTERNAL/CONFIDENTIAL/SECRET）")
    private String sensitivityLevel;
    
    @TableField("asset_status")
    @Schema(description = "资产状态（ACTIVE/DEPRECATED/ARCHIVED）")
    private String assetStatus;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}