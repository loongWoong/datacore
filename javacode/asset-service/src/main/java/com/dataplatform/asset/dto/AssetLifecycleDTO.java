package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "资产生命周期DTO")
public class AssetLifecycleDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "资产ID")
    private Long assetId;
    
    @Schema(description = "生命周期事件（CREATE/UPDATE/DEPRECATE/ARCHIVE/DELETE）")
    private String lifecycleEvent;
    
    @Schema(description = "事件描述")
    private String eventDescription;
    
    @Schema(description = "操作人")
    private String operator;
    
    @Schema(description = "操作时间")
    private Date operatedTime;
}