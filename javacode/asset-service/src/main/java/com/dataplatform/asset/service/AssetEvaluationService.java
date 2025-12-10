package com.dataplatform.asset.service;

import com.dataplatform.asset.dto.*;

import java.util.List;

public interface AssetEvaluationService {
    
    /**
     * 创建资产评估
     */
    AssetEvaluationDTO createEvaluation(CreateEvaluationDTO createDTO);
    
    /**
     * 获取资产评估历史
     */
    List<AssetEvaluationDTO> getEvaluations(Long assetId);
    
    /**
     * 获取资产评估趋势
     */
    List<EvaluationTrendDTO> getEvaluationTrend(Long assetId);
}