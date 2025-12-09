# 调度服务详细设计方案

## 1. 概述

调度服务是数据中台的重要基础设施服务，负责管理和执行各种定时任务和调度作业。该服务基于Spring Boot框架构建，集成了XXL-JOB分布式调度框架，提供高可用、高可靠的分布式任务调度能力。服务支持多种任务类型，包括Shell脚本、Java程序、Python脚本等，并提供完善的任务管理、监控和告警功能。

## 2. 技术架构

### 2.1 技术选型
- **框架**：Spring Boot 3.x + Spring Cloud Alibaba
- **调度框架**：XXL-JOB 2.4.x
- **数据库**：MySQL 8.x
- **注册中心**：Nacos
- **配置中心**：Nacos
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

### 2.2 服务架构图
```
┌─────────────────────────────────────────────────────────────┐
│                 Scheduler Service API Layer                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 任务管理 │  │ 执行器管理│  │ 日志监控 │  │ 配置管理 │   │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 任务服务 │  │ 执行器服务│  │ 日志服务 │  │ 配置服务 │   │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 任务仓储 │  │ 执行器仓储│  │ 日志仓储 │  │ 配置仓储 │   │
│  │ Repository│  │ Repository│  │ Repository│  │ Repository│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │ MySQL    │  │ Redis    │  │ Elasticsearch              │
│  │ (主存储)  │  │ (缓存)    │  │ (日志搜索)                 │
│  └──────────┘  └──────────┘  └──────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## 3. 数据库设计

### 3.1 MySQL表结构设计（XXL-JOB内置表）

#### 3.1.1 任务信息表 (xxl_job_info)
```sql
CREATE TABLE xxl_job_info (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_group int(11) NOT NULL COMMENT '执行器主键ID',
    job_desc varchar(255) NOT NULL COMMENT '任务描述',
    add_time datetime DEFAULT NULL COMMENT '添加时间',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    author varchar(64) DEFAULT NULL COMMENT '作者',
    alarm_email varchar(255) DEFAULT NULL COMMENT '报警邮件',
    schedule_type varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
    schedule_conf varchar(128) DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    misfire_strategy varchar(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    executor_route_strategy varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
    executor_handler varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    executor_param text COMMENT '执行器任务参数',
    executor_block_strategy varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
    executor_timeout int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
    executor_fail_retry_count int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    glue_type varchar(50) NOT NULL COMMENT 'GLUE类型',
    glue_source mediumtext COMMENT 'GLUE源代码',
    glue_remark varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
    glue_updatetime datetime DEFAULT NULL COMMENT 'GLUE更新时间',
    child_jobid varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    trigger_status tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
    trigger_last_time bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
    trigger_next_time bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务信息表';
```

#### 3.1.2 执行器信息表 (xxl_job_log)
```sql
CREATE TABLE xxl_job_log (
    id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_group int(11) NOT NULL COMMENT '执行器主键ID',
    job_id int(11) NOT NULL COMMENT '任务，主键ID',
    executor_address varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    executor_handler varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    executor_param text COMMENT '执行器任务参数',
    executor_sharding_param varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    executor_fail_retry_count int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    trigger_time datetime DEFAULT NULL COMMENT '调度-时间',
    trigger_code int(11) NOT NULL COMMENT '调度-结果',
    trigger_msg text COMMENT '调度-日志',
    handle_time datetime DEFAULT NULL COMMENT '执行-时间',
    handle_code int(11) NOT NULL COMMENT '执行-状态',
    handle_msg text COMMENT '执行-日志',
    alarm_status tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认，1-无需告警，2-告警成功，3-告警失败',
    PRIMARY KEY (id),
    KEY I_trigger_time (trigger_time),
    KEY I_handle_code (handle_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调度日志表';
```

#### 3.1.3 执行器注册表 (xxl_job_registry)
```sql
CREATE TABLE xxl_job_registry (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    registry_group varchar(50) NOT NULL COMMENT '注册组',
    registry_key varchar(255) NOT NULL COMMENT '注册键',
    registry_value varchar(255) NOT NULL COMMENT '注册值',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX I_g_k_v (registry_group, registry_key, registry_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行器注册表';
```

#### 3.1.4 任务日志报告表 (xxl_job_log_report)
```sql
CREATE TABLE xxl_job_log_report (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    trigger_day datetime DEFAULT NULL COMMENT '调度时间',
    running_count int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
    suc_count int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
    fail_count int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY I_trigger_day (trigger_day)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志报告表';
```

#### 3.1.5 任务日志清理标记表 (xxl_job_logglue)
```sql
CREATE TABLE xxl_job_logglue (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_id int(11) NOT NULL COMMENT '任务，主键ID',
    glue_type varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
    glue_source mediumtext COMMENT 'GLUE源代码',
    glue_remark varchar(128) NOT NULL COMMENT 'GLUE备注',
    add_time datetime DEFAULT NULL COMMENT '添加时间',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务GLUE日志表';
```

## 4. 核心功能实现

### 4.1 任务管理功能

#### 4.1.1 任务管理接口
```java
@RestController
@RequestMapping("/api/scheduler/jobs")
public class JobInfoController {
    
    @Autowired
    private JobInfoService jobInfoService;
    
    /**
     * 创建任务
     */
    @PostMapping
    public ResponseEntity<JobInfoDTO> createJob(@RequestBody CreateJobDTO createDTO) {
        JobInfoDTO job = jobInfoService.createJob(createDTO);
        return ResponseEntity.ok(job);
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping
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
    public ResponseEntity<JobInfoDetailDTO> getJobDetail(@PathVariable Integer id) {
        JobInfoDetailDTO detail = jobInfoService.getJobDetail(id);
        return ResponseEntity.ok(detail);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobInfoDTO> updateJob(@PathVariable Integer id, @RequestBody UpdateJobDTO updateDTO) {
        JobInfoDTO job = jobInfoService.updateJob(id, updateDTO);
        return ResponseEntity.ok(job);
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public