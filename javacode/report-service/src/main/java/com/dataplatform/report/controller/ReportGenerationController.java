package com.dataplatform.report.controller;

import com.dataplatform.report.dto.*;
import com.dataplatform.report.service.ReportGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/generate")
@Tag(name = "报表生成", description = "报表生成管理接口")
public class ReportGenerationController {
    
    @Autowired
    private ReportGenerationService reportGenerationService;
    
    /**
     * 生成报表
     */
    @PostMapping
    @Operation(summary = "生成报表", description = "根据配置生成报表")
    public ResponseEntity<ReportGenerationResultDTO> generateReport(@RequestBody GenerateReportDTO generateDTO) {
        ReportGenerationResultDTO result = reportGenerationService.generateReport(generateDTO);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取生成记录列表
     */
    @GetMapping("/records")
    @Operation(summary = "获取生成记录列表", description = "查询报表生成记录列表")
    public ResponseEntity<PageResult<ReportGenerationRecordDTO>> getGenerationRecords(
            @RequestParam(required = false) Long reportId,
            @RequestParam(required = false) String generationStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        GenerationRecordQuery query = GenerationRecordQuery.builder()
                .reportId(reportId)
                .generationStatus(generationStatus)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<ReportGenerationRecordDTO> pageResult = reportGenerationService.getGenerationRecords(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 下载报表文件
     */
    @GetMapping("/download/{recordId}")
    @Operation(summary = "下载报表文件", description = "根据记录ID下载生成的报表文件")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long recordId) {
        Resource fileResource = reportGenerationService.downloadReport(recordId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_file\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }
}