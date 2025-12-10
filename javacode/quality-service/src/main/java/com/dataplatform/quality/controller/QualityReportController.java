package com.dataplatform.quality.controller;

import com.dataplatform.quality.dto.*;
import com.dataplatform.quality.service.QualityReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/quality/reports")
@Tag(name = "质量报告", description = "数据质量报告管理接口")
public class QualityReportController {
    
    @Autowired
    private QualityReportService qualityReportService;
    
    /**
     * 获取质量报告列表
     */
    @GetMapping
    @Operation(summary = "获取质量报告列表", description = "根据条件查询质量报告列表")
    public ResponseEntity<PageResult<QualityReportDTO>> getReports(
            @Parameter(description = "表名") @RequestParam(required = false) String tableName,
            @Parameter(description = "开始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期") @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        QualityReportQuery query = QualityReportQuery.builder()
                .tableName(tableName)
                .startDate(startDate)
                .endDate(endDate)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<QualityReportDTO> pageResult = qualityReportService.getReports(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取表的质量趋势
     */
    @GetMapping("/trend/{tableName}")
    @Operation(summary = "获取表的质量趋势", description = "根据表名获取质量趋势数据")
    public ResponseEntity<List<QualityTrendDTO>> getQualityTrend(
            @Parameter(description = "表名") @PathVariable String tableName) {
        List<QualityTrendDTO> trend = qualityReportService.getQualityTrend(tableName);
        return ResponseEntity.ok(trend);
    }
}