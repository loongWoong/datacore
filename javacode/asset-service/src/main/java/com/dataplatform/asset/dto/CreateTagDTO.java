package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建标签DTO")
public class CreateTagDTO {
    
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
}