package com.dataplatform.quality.controller;

import com.dataplatform.quality.dto.*;
import com.dataplatform.quality.service.QualityCheckJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quality/jobs")
@Tag(name = "检查任务", description = "数据质量检查任务管理接口")
public class QualityCheckJobController {
    
    @Autowired
    private QualityCheckJobService qualityCheckJobService;
    
    /**
     * 创建检查任务
     */
    @PostMapping
    @Operation(summary = "创建检查任务", description = "创建新的质量检查任务")
    public ResponseEntity<QualityCheckJobDTO> createJob(
            @Parameter(description = "创建任务信息") @RequestBody CreateJobDTO createDTO) {
        QualityCheckJobDTO job = qualityCheckJobService.createJob(createDTO);
        return ResponseEntity.ok(job);
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping
    @Operation(summary = "获取任务列表", description = "根据条件查询质量检查任务列表")
    public ResponseEntity<PageResult<QualityCheckJobDTO>> getJobs(
            @Parameter(description = "任务名称") @RequestParam(required = false) String jobName,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<QualityCheckJobDTO> pageResult = qualityCheckJobService.getJobs(jobName, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 触发任务执行
     */
    @PostMapping("/{id}/trigger")
    @Operation(summary = "触发任务执行", description = "手动触发质量检查任务执行")
    public ResponseEntity<TriggerResultDTO> triggerJob(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        TriggerResultDTO result = qualityCheckJobService.triggerJob(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 启用/禁用任务
     */
    @PostMapping("/{id}/toggle")
    @Operation(summary = "启用/禁用任务", description = "启用或禁用质量检查任务")
    public ResponseEntity<Void> toggleJob(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        qualityCheckJobService.toggleJob(id, enabled);
        return ResponseEntity.ok().build();
    }
}