package com.dataplatform.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataplatform.quality.dto.*;
import com.dataplatform.quality.entity.QualityRule;
import com.dataplatform.quality.mapper.QualityRuleMapper;
import com.dataplatform.quality.service.QualityRuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualityRuleServiceImpl implements QualityRuleService {
    
    @Autowired
    private QualityRuleMapper qualityRuleMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public QualityRuleDTO createRule(CreateRuleDTO createDTO) {
        // 1. 校验规则编码唯一性
        QualityRule existingRule = qualityRuleMapper.selectByRuleCode(createDTO.getRuleCode());
        if (existingRule != null) {
            throw new RuntimeException("规则编码已存在");
        }
        
        // 2. 创建规则
        QualityRule rule = new QualityRule();
        BeanUtils.copyProperties(createDTO, rule);
        rule.setCreator("system"); // 实际项目中应从安全上下文中获取当前用户
        rule.setCreatedTime(new Date());
        rule.setUpdatedTime(new Date());
        qualityRuleMapper.insert(rule);
        
        // 3. 清除缓存
        redisTemplate.delete("quality:rules");
        
        // 4. 返回结果
        QualityRuleDTO result = new QualityRuleDTO();
        BeanUtils.copyProperties(rule, result);
        return result;
    }
    
    @Override
    public PageResult<QualityRuleDTO> getRules(String ruleType, String tableName, Integer pageNum, Integer pageSize) {
        // 1. 构造查询条件
        QualityRuleQuery query = QualityRuleQuery.builder()
                .ruleType(ruleType)
                .tableName(tableName)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        // 2. 查询数据
        PageHelper.startPage(pageNum, pageSize);
        List<QualityRule> rules = qualityRuleMapper.selectByCondition(query);
        
        // 3. 转换为DTO
        List<QualityRuleDTO> dtos = rules.stream()
                .map(rule -> {
                    QualityRuleDTO dto = new QualityRuleDTO();
                    BeanUtils.copyProperties(rule, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        // 4. 构造分页结果
        PageInfo<QualityRule> pageInfo = new PageInfo<>(rules);
        return PageResult.of(dtos, pageInfo.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public QualityRuleDTO getRule(Long id) {
        QualityRule rule = qualityRuleMapper.selectById(id);
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        QualityRuleDTO dto = new QualityRuleDTO();
        BeanUtils.copyProperties(rule, dto);
        return dto;
    }
    
    @Override
    @Transactional
    public QualityRuleDTO updateRule(Long id, UpdateRuleDTO updateDTO) {
        QualityRule existingRule = qualityRuleMapper.selectById(id);
        if (existingRule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingRule);
        existingRule.setUpdatedTime(new Date());
        qualityRuleMapper.updateById(existingRule);
        
        // 清除缓存
        redisTemplate.delete("quality:rules");
        
        QualityRuleDTO result = new QualityRuleDTO();
        BeanUtils.copyProperties(existingRule, result);
        return result;
    }
    
    @Override
    @Transactional
    public void deleteRule(Long id) {
        QualityRule existingRule = qualityRuleMapper.selectById(id);
        if (existingRule == null) {
            throw new RuntimeException("规则不存在");
        }
        
        qualityRuleMapper.deleteById(id);
        
        // 清除缓存
        redisTemplate.delete("quality:rules");
    }
}