package com.dataplatform.core.audit.service.impl;

import com.dataplatform.core.audit.entity.AuditLog;
import com.dataplatform.core.audit.dto.AuditLogDto;
import com.dataplatform.core.audit.repository.AuditLogRepository;
import com.dataplatform.core.audit.service.AuditLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Override
    public void saveAuditLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
    
    @Override
    public Page<AuditLogDto> findAuditLogs(Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findAll(pageable);
        return auditLogs.map(this::convertToDto);
    }
    
    @Override
    public AuditLogDto findAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id).orElse(null);
        return auditLog != null ? convertToDto(auditLog) : null;
    }
    
    private AuditLogDto convertToDto(AuditLog auditLog) {
        AuditLogDto dto = new AuditLogDto();
        BeanUtils.copyProperties(auditLog, dto);
        return dto;
    }
}