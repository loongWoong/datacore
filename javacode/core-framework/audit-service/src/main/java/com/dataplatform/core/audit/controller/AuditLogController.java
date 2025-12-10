package com.dataplatform.core.audit.controller;

import com.dataplatform.core.audit.dto.AuditLogDto;
import com.dataplatform.core.audit.service.AuditLogService;
import com.dataplatform.core.common.model.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit/logs")
@Tag(name = "审计日志接口", description = "审计日志管理接口")
public class AuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;
    
    @GetMapping
    @Operation(summary = "分页查询审计日志", description = "分页查询审计日志列表")
    public ResponseResult<Page<AuditLogDto>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AuditLogDto> auditLogs = auditLogService.findAuditLogs(pageable);
        return ResponseResult.success("查询成功", auditLogs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询审计日志", description = "根据ID查询审计日志详情")
    public ResponseResult<AuditLogDto> getAuditLogById(@PathVariable Long id) {
        AuditLogDto auditLog = auditLogService.findAuditLogById(id);
        return ResponseResult.success("查询成功", auditLog);
    }
}