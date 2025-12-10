package com.dataplatform.lineage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "dbt构建请求DTO")
public class DbtBuildRequestDTO {
    
    @Schema(description = "项目路径")
    private String projectPath;
    
    @Schema(description = "是否强制重建")
    private Boolean forceRebuild;
}