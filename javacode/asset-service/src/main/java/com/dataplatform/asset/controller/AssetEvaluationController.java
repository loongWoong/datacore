package com.dataplatform.asset.controller;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.service.AssetEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets/evaluations")
@Tag(name = "资产评估", description = "数据资产评估管理接口")
public class AssetEvaluationController {
    
    @Autowired
    private AssetEvaluationService assetEvaluationService;
    
    /**
     * 创建资产评估
     */
    @PostMapping
    @Operation(summary = "创建资产评估", description = "创建新的资产评估记录")
    public ResponseEntity<AssetEvaluationDTO> createEvaluation(
            @Parameter(description = "创建评估信息") @RequestBody CreateEvaluationDTO createDTO) {
        AssetEvaluationDTO evaluation = assetEvaluationService.createEvaluation(createDTO);
        return ResponseEntity.ok(evaluation);
    }
    
    /**
     * 获取资产评估历史
     */
    @GetMapping("/asset/{assetId}")
    @Operation(summary = "获取资产评估历史", description = "根据资产ID获取评估历史记录")
    public ResponseEntity<List<AssetEvaluationDTO>> getEvaluations(
            @Parameter(description = "资产ID") @PathVariable Long assetId) {
        List<AssetEvaluationDTO> evaluations = assetEvaluationService.getEvaluations(assetId);
        return ResponseEntity.ok(evaluations);
    }
    
    /**
     * 获取资产评估趋势
     */
    @GetMapping("/trend/{assetId}")
    @Operation(summary = "获取资产评估趋势", description = "根据资产ID获取评估趋势数据")
    public ResponseEntity<List<EvaluationTrendDTO>> getEvaluationTrend(
            @Parameter(description = "资产ID") @PathVariable Long assetId) {
        List<EvaluationTrendDTO> trend = assetEvaluationService.getEvaluationTrend(assetId);
        return ResponseEntity.ok(trend);
    }
}