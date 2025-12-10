package com.dataplatform.report.controller;

import com.dataplatform.report.dto.*;
import com.dataplatform.report.service.ReportConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/config")
@Tag(name = "报表配置", description = "报表配置管理接口")
public class ReportConfigController {
    
    @Autowired
    private ReportConfigService reportConfigService;
    
    /**
     * 创建报表配置
     */
    @PostMapping
    @Operation(summary = "创建报表配置", description = "创建新的报表配置")
    public ResponseEntity<ReportConfigDTO> createReportConfig(@RequestBody CreateReportConfigDTO createDTO) {
        ReportConfigDTO config = reportConfigService.createReportConfig(createDTO);
        return ResponseEntity.ok(config);
    }
    
    /**
     * 获取报表配置列表
     */
    @GetMapping
    @Operation(summary = "获取报表配置列表", description = "根据条件查询报表配置列表")
    public ResponseEntity<PageResult<ReportConfigDTO>> getReportConfigs(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String reportName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        ReportConfigQuery query = ReportConfigQuery.builder()
                .reportType(reportType)
                .reportName(reportName)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<ReportConfigDTO> pageResult = reportConfigService.getReportConfigs(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取报表配置详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取报表配置详情", description = "根据ID获取报表配置详情")
    public ResponseEntity<ReportConfigDetailDTO> getReportConfigDetail(@PathVariable Long id) {
        ReportConfigDetailDTO detail = reportConfigService.getReportConfigDetail(id);
        return ResponseEntity.ok(detail);
    }
    
    /**
     * 更新报表配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新报表配置", description = "根据ID更新报表配置")
    public ResponseEntity<ReportConfigDTO> updateReportConfig(@PathVariable Long id, @RequestBody UpdateReportConfigDTO updateDTO) {
        ReportConfigDTO config = reportConfigService.updateReportConfig(id, updateDTO);
        return ResponseEntity.ok(config);
    }
    
    /**
     * 删除报表配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除报表配置", description = "根据ID删除报表配置")
    public ResponseEntity<Void> deleteReportConfig(@PathVariable Long id) {
        reportConfigService.deleteReportConfig(id);
        return ResponseEntity.noContent().build();
    }
}