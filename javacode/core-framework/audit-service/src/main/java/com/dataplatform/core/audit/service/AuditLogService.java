package com.dataplatform.core.audit.service;

import com.dataplatform.core.audit.entity.AuditLog;
import com.dataplatform.core.audit.dto.AuditLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    
    void saveAuditLog(AuditLog auditLog);
    
    Page<AuditLogDto> findAuditLogs(Pageable pageable);
    
    AuditLogDto findAuditLogById(Long id);
}