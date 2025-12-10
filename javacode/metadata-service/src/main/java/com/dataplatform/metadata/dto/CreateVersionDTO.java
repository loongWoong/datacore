package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建版本信息DTO")
public class CreateVersionDTO {
    
    @Schema(description = "表ID")
    private Long tableId;
    
    @Schema(description = "版本号")
    private String versionNumber;
    
    @Schema(description = "版本描述")
    private String versionDescription;
}