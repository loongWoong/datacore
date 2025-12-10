package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "版本信息DTO")
public class VersionInfoDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "表ID")
    private Long tableId;
    
    @Schema(description = "版本号")
    private String versionNumber;
    
    @Schema(description = "版本描述")
    private String versionDescription;
    
    @Schema(description = "元数据内容")
    private String metadataContent;
    
    @Schema(description = "创建人")
    private String createdBy;
    
    @Schema(description = "创建时间")
    private Date createdTime;
}