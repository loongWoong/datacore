package com.dataplatform.scheduler.controller;

import com.dataplatform.scheduler.dto.*;
import com.dataplatform.scheduler.service.JobGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler/groups")
@Tag(name = "执行器管理", description = "执行器管理接口")
public class JobGroupController {
    
    @Autowired
    private JobGroupService jobGroupService;
    
    /**
     * 创建执行器
     */
    @PostMapping
    @Operation(summary = "创建执行器", description = "创建新的执行器")
    public ResponseEntity<JobGroupDTO> createJobGroup(@RequestBody CreateJobGroupDTO createDTO) {
        JobGroupDTO group = jobGroupService.createJobGroup(createDTO);
        return ResponseEntity.ok(group);
    }
    
    /**
     * 获取执行器列表
     */
    @GetMapping
    @Operation(summary = "获取执行器列表", description = "查询执行器列表")
    public ResponseEntity<PageResult<JobGroupDTO>> getJobGroups() {
        PageResult<JobGroupDTO> pageResult = jobGroupService.getJobGroups();
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取执行器详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取执行器详情", description = "根据ID获取执行器详情")
    public ResponseEntity<JobGroupDTO> getJobGroupDetail(@PathVariable Integer id) {
        JobGroupDTO detail = jobGroupService.getJobGroupDetail(id);
        return ResponseEntity.ok(detail);
    }
    
    /**
     * 更新执行器
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新执行器", description = "根据ID更新执行器")
    public ResponseEntity<JobGroupDTO> updateJobGroup(@PathVariable Integer id, @RequestBody UpdateJobGroupDTO updateDTO) {
        JobGroupDTO group = jobGroupService.updateJobGroup(id, updateDTO);
        return ResponseEntity.ok(group);
    }
    
    /**
     * 删除执行器
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除执行器", description = "根据ID删除执行器")
    public ResponseEntity<Void> deleteJobGroup(@PathVariable Integer id) {
        jobGroupService.deleteJobGroup(id);
        return ResponseEntity.noContent().build();
    }
}