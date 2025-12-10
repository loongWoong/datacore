package com.dataplatform.metadata.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("metadata_version_info")
@Schema(description = "元数据版本信息表")
public class VersionInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("table_id")
    @Schema(description = "表ID")
    private Long tableId;
    
    @TableField("version_number")
    @Schema(description = "版本号")
    private String versionNumber;
    
    @TableField("version_description")
    @Schema(description = "版本描述")
    private String versionDescription;
    
    @TableField("metadata_content")
    @Schema(description = "元数据内容")
    private String metadataContent;
    
    @TableField("created_by")
    @Schema(description = "创建人")
    private String createdBy;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}