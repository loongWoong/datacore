package com.dataplatform.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("asset_tag_relation")
@Schema(description = "资产标签关联表")
public class AssetTagRelation {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("asset_id")
    @Schema(description = "资产ID")
    private Long assetId;
    
    @TableField("tag_id")
    @Schema(description = "标签ID")
    private Long tagId;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
}