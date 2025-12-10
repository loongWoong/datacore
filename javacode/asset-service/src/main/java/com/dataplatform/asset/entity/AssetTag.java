package com.dataplatform.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("asset_tag")
@Schema(description = "资产标签表")
public class AssetTag {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("tag_code")
    @Schema(description = "标签编码")
    private String tagCode;
    
    @TableField("tag_name")
    @Schema(description = "标签名称")
    private String tagName;
    
    @TableField("tag_description")
    @Schema(description = "标签描述")
    private String tagDescription;
    
    @TableField("tag_category")
    @Schema(description = "标签分类（业务标签/技术标签/安全标签等）")
    private String tagCategory;
    
    @TableField("tag_color")
    @Schema(description = "标签颜色")
    private String tagColor;
    
    @TableField("creator")
    @Schema(description = "创建人")
    private String creator;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}