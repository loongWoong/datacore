package com.dataplatform.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "资产详情DTO")
public class AssetDetailDTO extends AssetInfoDTO {
    
    @Schema(description = "标签列表")
    private List<AssetTagDTO> tags;
    
    @Schema(description = "最新评估信息")
    private AssetEvaluationDTO latestEvaluation;
    
    @Schema(description = "生命周期记录列表")
    private List<AssetLifecycleDTO> lifecycles;
}