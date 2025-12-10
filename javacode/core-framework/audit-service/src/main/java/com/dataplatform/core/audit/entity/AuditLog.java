package com.dataplatform.core.audit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_audit_log")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "operation")
    private String operation;
    
    @Column(name = "method")
    private String method;
    
    @Column(name = "params")
    private String params;
    
    @Column(name = "ip")
    private String ip;
    
    @Column(name = "uri")
    private String uri;
    
    @Column(name = "http_method")
    private String httpMethod;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "execution_time")
    private Long executionTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}