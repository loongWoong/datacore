package com.dataplatform.scheduler.controller;

import com.dataplatform.scheduler.dto.*;
import com.dataplatform.scheduler.service.JobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler/logs")
@Tag(name = "任务日志", description = "任务日志管理接口")
public class JobLogController {
    
    @Autowired
    private JobLogService jobLogService;
    
    /**
     * 获取任务日志列表
     */
    @GetMapping
    @Operation(summary = "获取任务日志列表", description = "根据条件查询任务日志列表")
    public ResponseEntity<PageResult<JobLogDTO>> getJobLogs(
            @RequestParam(required = false) Integer jobGroup,
            @RequestParam(required = false) Integer jobId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        JobLogQuery query = JobLogQuery.builder()
                .jobGroup(jobGroup)
                .jobId(jobId)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<JobLogDTO> pageResult = jobLogService.getJobLogs(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取任务日志详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务日志详情", description = "根据ID获取任务日志详情")
    public ResponseEntity<JobLogDTO> getJobLogDetail(@PathVariable Long id) {
        JobLogDTO detail = jobLogService.getJobLogDetail(id);
        return ResponseEntity.ok(detail);
    }
    
    /**
     * 清理任务日志
     */
    @DeleteMapping("/clear/{jobId}")
    @Operation(summary = "清理任务日志", description = "根据任务ID清理任务日志")
    public ResponseEntity<Void> clearJobLogs(@PathVariable Integer jobId) {
        jobLogService.clearJobLogs(jobId);
        return ResponseEntity.noContent().build();
    }
}