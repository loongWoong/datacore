package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "资产打标签DTO")
public class TagAssetDTO {
    
    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}