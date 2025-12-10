package com.dataplatform.scheduler.controller;

import com.dataplatform.scheduler.dto.*;
import com.dataplatform.scheduler.service.JobInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler/jobs")
@Tag(name = "任务管理", description = "任务管理接口")
public class JobInfoController {
    
    @Autowired
    private JobInfoService jobInfoService;
    
    /**
     * 创建任务
     */
    @PostMapping
    @Operation(summary = "创建任务", description = "创建新的调度任务")
    public ResponseEntity<JobInfoDTO> createJob(@RequestBody CreateJobDTO createDTO) {
        JobInfoDTO job = jobInfoService.createJob(createDTO);
        return ResponseEntity.ok(job);
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping
    @Operation(summary = "获取任务列表", description = "根据条件查询任务列表")
    public ResponseEntity<PageResult<JobInfoDTO>> getJobs(
            @RequestParam(required = false) String jobDesc,
            @RequestParam(required = false) Integer jobGroup,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        JobInfoQuery query = JobInfoQuery.builder()
                .jobDesc(jobDesc)
                .jobGroup(jobGroup)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<JobInfoDTO> pageResult = jobInfoService.getJobs(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务详情", description = "根据ID获取任务详情")
    public ResponseEntity<JobInfoDetailDTO> getJobDetail(@PathVariable Integer id) {
        JobInfoDetailDTO detail = jobInfoService.getJobDetail(id);
        return ResponseEntity.ok(detail);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新任务", description = "根据ID更新任务")
    public ResponseEntity<JobInfoDTO> updateJob(@PathVariable Integer id, @RequestBody UpdateJobDTO updateDTO) {
        JobInfoDTO job = jobInfoService.updateJob(id, updateDTO);
        return ResponseEntity.ok(job);
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务", description = "根据ID删除任务")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer id) {
        jobInfoService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 启动任务
     */
    @PostMapping("/{id}/start")
    @Operation(summary = "启动任务", description = "根据ID启动任务")
    public ResponseEntity<Void> startJob(@PathVariable Integer id) {
        jobInfoService.startJob(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 停止任务
     */
    @PostMapping("/{id}/stop")
    @Operation(summary = "停止任务", description = "根据ID停止任务")
    public ResponseEntity<Void> stopJob(@PathVariable Integer id) {
        jobInfoService.stopJob(id);
        return ResponseEntity.ok().build();
    }
}