package com.dataplatform.quality.service;

import com.dataplatform.quality.dto.*;

public interface QualityRuleService {
    
    /**
     * 创建质量规则
     */
    QualityRuleDTO createRule(CreateRuleDTO createDTO);
    
    /**
     * 获取规则列表
     */
    PageResult<QualityRuleDTO> getRules(String ruleType, String tableName, Integer pageNum, Integer pageSize);
    
    /**
     * 获取规则详情
     */
    QualityRuleDTO getRule(Long id);
    
    /**
     * 更新规则
     */
    QualityRuleDTO updateRule(Long id, UpdateRuleDTO updateDTO);
    
    /**
     * 删除规则
     */
    void deleteRule(Long id);
}