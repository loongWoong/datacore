package com.dataplatform.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("asset_lifecycle")
@Schema(description = "资产生命周期表")
public class AssetLifecycle {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("asset_id")
    @Schema(description = "资产ID")
    private Long assetId;
    
    @TableField("lifecycle_event")
    @Schema(description = "生命周期事件（CREATE/UPDATE/DEPRECATE/ARCHIVE/DELETE）")
    private String lifecycleEvent;
    
    @TableField("event_description")
    @Schema(description = "事件描述")
    private String eventDescription;
    
    @TableField("operator")
    @Schema(description = "操作人")
    private String operator;
    
    @TableField(value = "operated_time", fill = FieldFill.INSERT)
    @Schema(description = "操作时间")
    private Date operatedTime;
}