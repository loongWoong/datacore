package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "资产标签DTO")
public class AssetTagDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "标签编码")
    private String tagCode;
    
    @Schema(description = "标签名称")
    private String tagName;
    
    @Schema(description = "标签描述")
    private String tagDescription;
    
    @Schema(description = "标签分类（业务标签/技术标签/安全标签等）")
    private String tagCategory;
    
    @Schema(description = "标签颜色")
    private String tagColor;
    
    @Schema(description = "创建人")
    private String creator;
    
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Schema(description = "更新时间")
    private Date updatedTime;
}