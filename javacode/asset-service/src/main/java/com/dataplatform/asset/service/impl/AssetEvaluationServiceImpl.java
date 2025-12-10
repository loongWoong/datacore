package com.dataplatform.asset.service.impl;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.entity.AssetEvaluation;
import com.dataplatform.asset.mapper.AssetEvaluationMapper;
import com.dataplatform.asset.service.AssetEvaluationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetEvaluationServiceImpl implements AssetEvaluationService {
    
    @Autowired
    private AssetEvaluationMapper assetEvaluationMapper;
    
    @Override
    public AssetEvaluationDTO createEvaluation(CreateEvaluationDTO createDTO) {
        // 1. 创建评估记录
        AssetEvaluation evaluation = new AssetEvaluation();
        BeanUtils.copyProperties(createDTO, evaluation);
        evaluation.setEvaluationDate(new Date());
        evaluation.setCreatedTime(new Date());
        assetEvaluationMapper.insert(evaluation);
        
        // 2. 返回结果
        AssetEvaluationDTO result = new AssetEvaluationDTO();
        BeanUtils.copyProperties(evaluation, result);
        return result;
    }
    
    @Override
    public List<AssetEvaluationDTO> getEvaluations(Long assetId) {
        List<AssetEvaluation> evaluations = assetEvaluationMapper.selectByAssetId(assetId);
        return evaluations.stream()
                .map(evaluation -> {
                    AssetEvaluationDTO dto = new AssetEvaluationDTO();
                    BeanUtils.copyProperties(evaluation, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EvaluationTrendDTO> getEvaluationTrend(Long assetId) {
        // 查询最近几次的评估记录
        List<AssetEvaluation> evaluations = assetEvaluationMapper.selectByAssetId(assetId);
        
        // 转换为趋势DTO
        return evaluations.stream()
                .map(evaluation -> {
                    EvaluationTrendDTO trendDTO = new EvaluationTrendDTO();
                    trendDTO.setEvaluationDate(evaluation.getEvaluationDate());
                    trendDTO.setOverallScore(evaluation.getOverallScore());
                    trendDTO.setBusinessValue(evaluation.getBusinessValue());
                    trendDTO.setTechnicalQuality(evaluation.getTechnicalQuality());
                    trendDTO.setSecurityLevel(evaluation.getSecurityLevel());
                    return trendDTO;
                })
                .collect(Collectors.toList());
    }
}