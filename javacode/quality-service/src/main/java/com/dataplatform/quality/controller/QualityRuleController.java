package com.dataplatform.quality.controller;

import com.dataplatform.quality.dto.*;
import com.dataplatform.quality.service.QualityRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quality/rules")
@Tag(name = "质量规则", description = "数据质量规则管理接口")
public class QualityRuleController {
    
    @Autowired
    private QualityRuleService qualityRuleService;
    
    /**
     * 创建质量规则
     */
    @PostMapping
    @Operation(summary = "创建质量规则", description = "创建新的数据质量规则")
    public ResponseEntity<QualityRuleDTO> createRule(
            @Parameter(description = "创建规则信息") @RequestBody CreateRuleDTO createDTO) {
        QualityRuleDTO rule = qualityRuleService.createRule(createDTO);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * 获取规则列表
     */
    @GetMapping
    @Operation(summary = "获取规则列表", description = "根据条件查询质量规则列表")
    public ResponseEntity<PageResult<QualityRuleDTO>> getRules(
            @Parameter(description = "规则类型") @RequestParam(required = false) String ruleType,
            @Parameter(description = "表名") @RequestParam(required = false) String tableName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<QualityRuleDTO> pageResult = qualityRuleService.getRules(ruleType, tableName, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取规则详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取规则详情", description = "根据ID获取质量规则详情")
    public ResponseEntity<QualityRuleDTO> getRule(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        QualityRuleDTO rule = qualityRuleService.getRule(id);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * 更新规则
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新规则", description = "更新数据质量规则")
    public ResponseEntity<QualityRuleDTO> updateRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Parameter(description = "更新规则信息") @RequestBody UpdateRuleDTO updateDTO) {
        QualityRuleDTO rule = qualityRuleService.updateRule(id, updateDTO);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * 删除规则
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除规则", description = "删除数据质量规则")
    public ResponseEntity<Void> deleteRule(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        qualityRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}