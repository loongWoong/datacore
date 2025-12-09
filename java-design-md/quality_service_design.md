# 数据质量服务详细设计方案

## 1. 概述

数据质量服务是数据中台的重要组成部分，负责定义、执行和监控数据质量规则，确保数据的完整性、准确性、一致性和可靠性。该服务基于Spring Boot框架构建，集成了Quartz调度框架用于定时执行质量检查任务，使用MySQL存储质量规则和检查结果，提供全面的数据质量管理功能。

## 2. 技术架构

### 2.1 技术选型
- **框架**：Spring Boot 3.x + Spring Cloud Alibaba
- **调度框架**：Quartz 2.3.x
- **数据库**：MySQL 8.x
- **缓存**：Redis 7.x
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

### 2.2 服务架构图
```
┌─────────────────────────────────────────────────────────────┐
│                   Quality Service API Layer                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 规则管理 │  │ 检查执行 │  │ 报告查询 │  │ 配置管理 │   │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 规则服务 │  │ 检查服务 │  │ 报告服务 │  │ 配置服务 │   │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 规则仓储 │  │ 检查仓储 │  │ 报告仓储 │  │ 配置仓储 │   │
│  │ Repository│  │ Repository│  │ Repository│  │ Repository│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ MySQL    │  │ Redis    │  │ Elasticsearch              │   │
│  │ (主存储)  │  │ (缓存)    │  │ (日志搜索)                 │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 3. 数据库设计

### 3.1 MySQL表结构设计

#### 3.1.1 质量规则表 (quality_rule)
```sql
CREATE TABLE quality_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_code VARCHAR(128) NOT NULL COMMENT '规则编码',
    rule_name VARCHAR(256) NOT NULL COMMENT '规则名称',
    rule_description TEXT COMMENT '规则描述',
    rule_type VARCHAR(64) NOT NULL COMMENT '规则类型（完整性、准确性、一致性、唯一性等）',
    table_name VARCHAR(256) NOT NULL COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名（适用于字段级规则）',
    check_expression TEXT COMMENT '检查表达式（SQL片段）',
    expected_value VARCHAR(512) COMMENT '期望值',
    threshold DECIMAL(10,4) DEFAULT 0.0000 COMMENT '阈值',
    severity_level VARCHAR(32) DEFAULT 'MEDIUM' COMMENT '严重级别（LOW/MEDIUM/HIGH）',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_rule_code (rule_code),
    INDEX idx_table_name (table_name),
    INDEX idx_rule_type (rule_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据质量规则表';
```

#### 3.1.2 质量检查任务表 (quality_check_job)
```sql
CREATE TABLE quality_check_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    job_name VARCHAR(256) NOT NULL COMMENT '任务名称',
    job_description TEXT COMMENT '任务描述',
    rule_ids TEXT COMMENT '关联的规则ID列表（逗号分隔）',
    cron_expression VARCHAR(128) COMMENT 'Cron表达式',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    last_execute_time DATETIME COMMENT '最后执行时间',
    next_execute_time DATETIME COMMENT '下次执行时间',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_next_execute_time (next_execute_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检查任务表';
```

#### 3.1.3 质量检查结果表 (quality_check_result)
```sql
CREATE TABLE quality_check_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    job_id BIGINT NOT NULL COMMENT '任务ID',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    table_name VARCHAR(256) NOT NULL COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名',
    check_date DATE NOT NULL COMMENT '检查日期',
    check_status VARCHAR(32) NOT NULL COMMENT '检查状态（PASS/FAIL/WARNING）',
    actual_value DECIMAL(20,4) COMMENT '实际值',
    expected_value DECIMAL(20,4) COMMENT '期望值',
    deviation DECIMAL(20,4) COMMENT '偏差值',
    failed_records LONGTEXT COMMENT '失败记录详情（JSON格式）',
    execution_time BIGINT COMMENT '执行耗时（毫秒）',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_job_id (job_id),
    INDEX idx_rule_id (rule_id),
    INDEX idx_table_name (table_name),
    INDEX idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检查结果表';
```

#### 3.1.4 质量报告表 (quality_report)
```sql
CREATE TABLE quality_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_date DATE NOT NULL COMMENT '报告日期',
    table_name VARCHAR(256) NOT NULL COMMENT '表名',
    overall_score DECIMAL(10,2) COMMENT '综合质量得分',
    completeness_rate DECIMAL(10,2) COMMENT '完整性率',
    accuracy_rate DECIMAL(10,2) COMMENT '准确率',
    consistency_rate DECIMAL(10,2) COMMENT '一致率',
    uniqueness_rate DECIMAL(10,2) COMMENT '唯一性率',
    timeliness_rate DECIMAL(10,2) COMMENT '及时性率',
    total_records BIGINT COMMENT '总记录数',
    failed_rules INT COMMENT '失败规则数',
    warning_rules INT COMMENT '警告规则数',
    passed_rules INT COMMENT '通过规则数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_report_date_table (report_date, table_name),
    INDEX idx_report_date (report_date),
    INDEX idx_table_name (table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量报告表';
```

## 4. 核心功能实现

### 4.1 质量规则管理

#### 4.1.1 规则定义与管理
```java
@RestController
@RequestMapping("/api/quality/rules")
public class QualityRuleController {
    
    @Autowired
    private QualityRuleService qualityRuleService;
    
    /**
     * 创建质量规则
     */
    @PostMapping
    public ResponseEntity<QualityRuleDTO> createRule(@RequestBody CreateRuleDTO createDTO) {
        QualityRuleDTO rule = qualityRuleService.createRule(createDTO);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * 获取规则列表
     */
    @GetMapping
    public ResponseEntity<PageResult<QualityRuleDTO>> getRules(
            @RequestParam(required = false) String ruleType,
            @RequestParam(required = false) String tableName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<QualityRuleDTO> pageResult = qualityRuleService.getRules(ruleType, tableName, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取规则详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<QualityRuleDTO> getRule(@PathVariable Long id) {
        QualityRuleDTO rule = qualityRuleService.getRule(id);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * 更新规则
     */
    @PutMapping("/{id}")
    public ResponseEntity<QualityRuleDTO> updateRule(@PathVariable Long id, @RequestBody UpdateRuleDTO updateDTO) {
        QualityRuleDTO rule = qualityRuleService.updateRule(id, updateDTO);
        return ResponseEntity.ok(rule);
    }
    
    /**
     * 删除规则
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        qualityRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### 4.1.2 规则服务实现
```java
@Service
public class QualityRuleServiceImpl implements QualityRuleService {
    
    @Autowired
    private QualityRuleMapper qualityRuleMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public QualityRuleDTO createRule(CreateRuleDTO createDTO) {
        // 1. 校验规则编码唯一性
        QualityRule existingRule = qualityRuleMapper.selectByRuleCode(createDTO.getRuleCode());
        if (existingRule != null) {
            throw new BusinessException("规则编码已存在");
        }
        
        // 2. 创建规则
        QualityRule rule = new QualityRule();
        BeanUtils.copyProperties(createDTO, rule);
        rule.setCreator(SecurityUtils.getCurrentUsername());
        rule.setCreatedTime(new Date());
        rule.setUpdatedTime(new Date());
        qualityRuleMapper.insert(rule);
        
        // 3. 清除缓存
        redisTemplate.delete("quality:rules");
        
        // 4. 返回结果
        QualityRuleDTO result = new QualityRuleDTO();
        BeanUtils.copyProperties(rule, result);
        return result;
    }
    
    @Override
    public PageResult<QualityRuleDTO> getRules(String ruleType, String tableName, Integer pageNum, Integer pageSize) {
        // 1. 构造查询条件
        QualityRuleQuery query = QualityRuleQuery.builder()
                .ruleType(ruleType)
                .tableName(tableName)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        // 2. 查询数据
        PageHelper.startPage(pageNum, pageSize);
        List<QualityRule> rules = qualityRuleMapper.selectByCondition(query);
        
        // 3. 转换为DTO
        List<QualityRuleDTO> dtos = rules.stream()
                .map(rule -> {
                    QualityRuleDTO dto = new QualityRuleDTO();
                    BeanUtils.copyProperties(rule, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        // 4. 构造分页结果
        PageInfo<QualityRule> pageInfo = new PageInfo<>(rules);
        return PageResult.of(dtos, pageInfo.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public QualityRuleDTO getRule(Long id) {
        QualityRule rule = qualityRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException("规则不存在");
        }
        
        QualityRuleDTO dto = new QualityRuleDTO();
        BeanUtils.copyProperties(rule, dto);
        return dto;
    }
    
    @Override
    @Transactional
    public QualityRuleDTO updateRule(Long id, UpdateRuleDTO updateDTO) {
        QualityRule existingRule = qualityRuleMapper.selectById(id);
        if (existingRule == null) {
            throw new BusinessException("规则不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingRule);
        existingRule.setUpdatedTime(new Date());
        qualityRuleMapper.updateById(existingRule);
        
        // 清除缓存
        redisTemplate.delete("quality:rules");
        
        QualityRuleDTO result = new QualityRuleDTO();
        BeanUtils.copyProperties(existingRule, result);
        return result;
    }
    
    @Override
    @Transactional
    public void deleteRule(Long id) {
        QualityRule existingRule = qualityRuleMapper.selectById(id);
        if (existingRule == null) {
            throw new BusinessException("规则不存在");
        }
        
        qualityRuleMapper.deleteById(id);
        
        // 清除缓存
        redisTemplate.delete("quality:rules");
    }
}
```

### 4.2 质量检查执行

#### 4.2.1 检查任务管理
```java
@RestController
@RequestMapping("/api/quality/jobs")
public class QualityCheckJobController {
    
    @Autowired
    private QualityCheckJobService qualityCheckJobService;
    
    /**
     * 创建检查任务
     */
    @PostMapping
    public ResponseEntity<QualityCheckJobDTO> createJob(@RequestBody CreateJobDTO createDTO) {
        QualityCheckJobDTO job = qualityCheckJobService.createJob(createDTO);
        return ResponseEntity.ok(job);
    }
    
    /**
     * 获取任务列表
     */
    @GetMapping
    public ResponseEntity<PageResult<QualityCheckJobDTO>> getJobs(
            @RequestParam(required = false) String jobName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<QualityCheckJobDTO> pageResult = qualityCheckJobService.getJobs(jobName, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 触发任务执行
     */
    @PostMapping("/{id}/trigger")
    public ResponseEntity<TriggerResultDTO> triggerJob(@PathVariable Long id) {
        TriggerResultDTO result = qualityCheckJobService.triggerJob(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 启用/禁用任务
     */
    @PostMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleJob(@PathVariable Long id, @RequestParam Boolean enabled) {
        qualityCheckJobService.toggleJob(id, enabled);
        return ResponseEntity.ok().build();
    }
}
```

#### 4.2.2 检查服务实现
```java
@Service
public class QualityCheckJobServiceImpl implements QualityCheckJobService {
    
    @Autowired
    private QualityCheckJobMapper qualityCheckJobMapper;
    
    @Autowired
    private QualityRuleMapper qualityRuleMapper;
    
    @Autowired
    private QualityCheckResultMapper qualityCheckResultMapper;
    
    @Autowired
    private Scheduler scheduler;
    
    @Override
    @Transactional
    public QualityCheckJobDTO createJob(CreateJobDTO createDTO) {
        // 1. 创建任务
        QualityCheckJob job = new QualityCheckJob();
        BeanUtils.copyProperties(createDTO, job);
        job.setCreator(SecurityUtils.getCurrentUsername());
        job.setCreatedTime(new Date());
        job.setUpdatedTime(new Date());
        qualityCheckJobMapper.insert(job);
        
        // 2. 注册Quartz任务
        registerQuartzJob(job);
        
        // 3. 返回结果
        QualityCheckJobDTO result = new QualityCheckJobDTO();
        BeanUtils.copyProperties(job, result);
        return result;
    }
    
    @Override
    public PageResult<QualityCheckJobDTO> getJobs(String jobName, Integer pageNum, Integer pageSize) {
        // 查询逻辑...
        return null;
    }
    
    @Override
    public TriggerResultDTO triggerJob(Long id) {
        QualityCheckJob job = qualityCheckJobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        
        // 执行质量检查
        List<QualityCheckResult> results = executeQualityCheck(job);
        
        // 保存检查结果
        for (QualityCheckResult result : results) {
            qualityCheckResultMapper.insert(result);
        }
        
        // 构造返回结果
        TriggerResultDTO dto = new TriggerResultDTO();
        dto.setSuccess(true);
        dto.setMessage("检查完成");
        dto.setResultCount(results.size());
        return dto;
    }
    
    @Override
    @Transactional
    public void toggleJob(Long id, Boolean enabled) {
        QualityCheckJob job = qualityCheckJobMapper.selectById(id);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        
        job.setEnabled(enabled);
        job.setUpdatedTime(new Date());
        qualityCheckJobMapper.updateById(job);
        
        // 更新Quartz任务状态
        updateQuartzJobStatus(job);
    }
    
    /**
     * 执行质量检查
     */
    private List<QualityCheckResult> executeQualityCheck(QualityCheckJob job) {
        List<QualityCheckResult> results = new ArrayList<>();
        
        // 1. 解析规则ID列表
        List<Long> ruleIds = Arrays.stream(job.getRuleIds().split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
        
        // 2. 获取规则详情
        List<QualityRule> rules = qualityRuleMapper.selectBatchIds(ruleIds);
        
        // 3. 执行每个规则的检查
        for (QualityRule rule : rules) {
            QualityCheckResult result = executeRuleCheck(rule);
            result.setJobId(job.getId());
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 执行单个规则检查
     */
    private QualityCheckResult executeRuleCheck(QualityRule rule) {
        QualityCheckResult result = new QualityCheckResult();
        result.setRuleId(rule.getId());
        result.setTableName(rule.getTableName());
        result.setColumnName(rule.getColumnName());
        result.setCheckDate(new Date());
        
        try {
            // 根据规则类型执行不同的检查逻辑
            switch (rule.getRuleType()) {
                case "COMPLETENESS":
                    executeCompletenessCheck(rule, result);
                    break;
                case "ACCURACY":
                    executeAccuracyCheck(rule, result);
                    break;
                case "CONSISTENCY":
                    executeConsistencyCheck(rule, result);
                    break;
                case "UNIQUENESS":
                    executeUniquenessCheck(rule, result);
                    break;
                default:
                    throw new BusinessException("不支持的规则类型: " + rule.getRuleType());
            }
        } catch (Exception e) {
            result.setCheckStatus("FAIL");
            result.setErrorMessage(e.getMessage());
        }
        
        result.setCreatedTime(new Date());
        return result;
    }
    
    /**
     * 完整性检查
     */
    private void executeCompletenessCheck(QualityRule rule, QualityCheckResult result) {
        String sql = String.format(
            "SELECT COUNT(*) as total_count, COUNT(%s) as non_null_count FROM %s",
            rule.getColumnName(),
            rule.getTableName()
        );
        
        // 执行SQL查询
        Map<String, Object> queryResult = jdbcTemplate.queryForMap(sql);
        long totalCount = ((Number) queryResult.get("total_count")).longValue();
        long nonNullCount = ((Number) queryResult.get("non_null_count")).longValue();
        
        double completenessRate = (double) nonNullCount / totalCount;
        result.setActualValue(BigDecimal.valueOf(completenessRate));
        result.setExpectedValue(rule.getExpectedValue() != null ? 
            new BigDecimal(rule.getExpectedValue()) : BigDecimal.ONE);
        
        // 判断检查结果
        if (completenessRate >= (1 - rule.getThreshold().doubleValue())) {
            result.setCheckStatus("PASS");
        } else if (completenessRate >= (1 - rule.getThreshold().doubleValue() * 2)) {
            result.setCheckStatus("WARNING");
        } else {
            result.setCheckStatus("FAIL");
        }
    }
    
    /**
     * 准确性检查
     */
    private void executeAccuracyCheck(QualityRule rule, QualityCheckResult result) {
        String checkExpression = rule.getCheckExpression();
        String sql = String.format("SELECT COUNT(*) as total_count, SUM(CASE WHEN %s THEN 1 ELSE 0 END) as accurate_count FROM %s",
            checkExpression, rule.getTableName());
        
        // 执行SQL查询
        Map<String, Object> queryResult = jdbcTemplate.queryForMap(sql);
        long totalCount = ((Number) queryResult.get("total_count")).longValue();
        long accurateCount = ((Number) queryResult.get("accurate_count")).longValue();
        
        double accuracyRate = (double) accurateCount / totalCount;
        result.setActualValue(BigDecimal.valueOf(accuracyRate));
        
        // 判断检查结果
        if (accuracyRate >= (1 - rule.getThreshold().doubleValue())) {
            result.setCheckStatus("PASS");
        } else if (accuracyRate >= (1 - rule.getThreshold().doubleValue() * 2)) {
            result.setCheckStatus("WARNING");
        } else {
            result.setCheckStatus("FAIL");
        }
    }
    
    // 其他检查类型的实现...
}
```

### 4.3 质量报告生成

#### 4.3.1 报告查询接口
```java
@RestController
@RequestMapping("/api/quality/reports")
public class QualityReportController {
    
    @Autowired
    private QualityReportService qualityReportService;
    
    /**
     * 获取质量报告列表
     */
    @GetMapping
    public ResponseEntity<PageResult<QualityReportDTO>> getReports(
            @RequestParam(required = false) String tableName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        QualityReportQuery query = QualityReportQuery.builder()
                .tableName(tableName)
                .startDate(startDate)
                .endDate(endDate)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<QualityReportDTO> pageResult = qualityReportService.getReports(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取表的质量趋势
     */
    @GetMapping("/trend/{tableName}")
    public ResponseEntity<List<QualityTrendDTO>> getQualityTrend(@PathVariable String tableName) {
        List<QualityTrendDTO> trend = qualityReportService.getQualityTrend(tableName);
        return ResponseEntity.ok(trend);
    }
}
```

## 5. 接口设计

### 5.1 质量规则接口

#### 5.1.1 创建质量规则
- **接口地址**：POST /api/quality/rules
- **请求体**：
```json
{
  "ruleCode": "COMPLETENESS_STG_TOLL_TRANS_ID",
  "ruleName": "收费交易ID完整性检查",
  "ruleDescription": "检查stg_toll_transaction表中transaction_id字段的完整性",
  "ruleType": "COMPLETENESS",
  "tableName": "main_staging.stg_toll_transaction",
  "columnName": "transaction_id",
  "threshold": 0.01,
  "severityLevel": "HIGH",
  "isEnabled": true
}
```

#### 5.1.2 获取规则列表
- **接口地址**：GET /api/quality/rules
- **请求参数**：
  - ruleType (String, optional)：规则类型
  - tableName (String, optional)：表名
  - pageNum (Integer, optional, default=1)：页码
  - pageSize (Integer, optional, default=10)：每页大小
- **响应示例**：
```json
{
  "data": [
    {
      "id": 1,
      "ruleCode": "COMPLETENESS_STG_TOLL_TRANS_ID",
      "ruleName": "收费交易ID完整性检查",
      "ruleDescription": "检查stg_toll_transaction表中transaction_id字段的完整性",
      "ruleType": "COMPLETENESS",
      "tableName": "main_staging.stg_toll_transaction",
      "columnName": "transaction_id",
      "threshold": 0.01,
      "severityLevel": "HIGH",
      "isEnabled": true,
      "creator": "admin",
      "createdTime": "2025-12-09T10:30:00",
      "updatedTime": "2025-12-09T10:30:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

### 5.2 质量检查任务接口

#### 5.2.1 创建检查任务
- **接口地址**：POST /api/quality/jobs
- **请求体**：
```json
{
  "jobName": "每日数据质量检查",
  "jobDescription": "每天凌晨2点执行的数据质量检查任务",
  "ruleIds": "1,2,3,4,5",
  "cronExpression": "0 0 2 * * ?",
  "isEnabled": true
}
```

#### 5.2.2 触发任务执行
- **接口地址**：POST /api/quality/jobs/{id}/trigger
- **路径参数**：
  - id (Long)：任务ID
- **响应示例**：
```json
{
  "success": true,
  "message": "检查完成",
  "resultCount": 5
}
```

### 5.3 质量报告接口

#### 5.3.1 获取质量报告列表
- **接口地址**：GET /api/quality/reports
- **请求参数**：
  - tableName (String, optional)：表名
  - startDate (String, optional)：开始日期（yyyy-MM-dd）
  - endDate (String, optional)：结束日期（yyyy-MM-dd）
  - pageNum (Integer, optional, default=1)：页码
  - pageSize (Integer, optional, default=10)：每页大小
- **响应示例**：
```json
{
  "data": [
    {
      "id": 1,
      "reportDate": "2025-12-09",
      "tableName": "main_staging.stg_toll_transaction",
      "overallScore": 95.5,
      "completenessRate": 98.2,
      "accuracyRate": 97.8,
      "consistencyRate": 96.5,
      "uniquenessRate": 100.0,
      "timelinessRate": 99.1,
      "totalRecords": 1000000,
      "failedRules": 1,
      "warningRules": 2,
      "passedRules": 15
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

## 6. 性能优化措施

### 6.1 数据库优化
1. **索引优化**：在常用查询字段上建立合适的索引
2. **分页查询**：对于大数据量的查询采用分页处理
3. **读写分离**：配置主从数据库，读操作走从库，写操作走主库

### 6.2 缓存策略
1. **规则缓存**：将常用的质量规则缓存到Redis中
2. **报告缓存**：将近期的质量报告缓存到Redis中
3. **热点数据缓存**：对频繁访问的质量数据进行缓存预热

### 6.3 异步处理
1. **检查任务异步执行**：质量检查任务采用异步方式进行，避免阻塞接口响应
2. **报告生成异步处理**：质量报告的生成采用异步处理方式
3. **批量操作**：提供批量创建、更新接口，减少网络开销

## 7. 安全设计

### 7.1 认证授权
1. **JWT Token**：使用JWT进行用户身份认证
2. **RBAC权限控制**：基于角色的访问控制，不同角色具有不同的操作权限
3. **接口权限校验**：每个接口都需要进行权限校验

### 7.2 数据安全
1. **敏感数据脱敏**：对检查结果中的敏感数据进行脱敏处理
2. **操作日志记录**：记录所有质量规则变更和检查操作日志
3. **数据备份**：定期对质量数据进行备份

## 8. 部署方案

### 8.1 部署架构
```
┌─────────────────────────────────────────────────────────────┐
│                    Load Balancer                            │
│                    (Nginx/Nacos)                            │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│   Instance 1    │ │   Instance 2    │ │   Instance N    │
│  ┌───────────┐  │ │  ┌───────────┐  │ │  ┌───────────┐  │
│  │ Quality   │  │ │  │ Quality   │  │ │  │ Quality   │  │
│  │ Service   │  │ │  │ Service   │  │ │  │ Service   │  │
│  └───────────┘  │ │  └───────────┘  │ │  └───────────┘  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                    Shared Storage                           │
│  ┌───────────┐  ┌───────────┐  ┌─────────────────────────┐ │
│  │   MySQL   │  │   Redis   │  │   Elasticsearch         │ │
│  │ (主存储)   │  │ (缓存)     │  │ (日志搜索)               │ │
│  └───────────┘  └───────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 8.2 部署步骤
1. **环境准备**：安装JDK 17、MySQL、Redis
2. **数据库初始化**：执行数据库脚本创建表结构
3. **配置文件调整**：根据实际环境修改application.yml配置
4. **服务打包**：使用Maven打包成jar文件
5. **服务部署**：将jar文件部署到服务器并启动
6. **负载均衡配置**：配置Nginx或Nacos进行负载均衡

## 9. 监控与运维

### 9.1 监控指标
1. **接口响应时间**：监控各接口的平均响应时间
2. **数据库连接数**：监控数据库连接池使用情况
3. **缓存命中率**：监控Redis缓存命中率
4. **任务执行成功率**：监控质量检查任务的执行成功率

### 9.2 日志管理
1. **操作日志**：记录所有用户操作日志
2. **错误日志**：记录系统错误和异常信息
3. **性能日志**：记录关键接口的性能数据
4. **审计日志**：记录质量规则变更的审计信息

### 9.3 告警机制
1. **接口超时告警**：接口响应时间超过阈值时告警
2. **数据库连接告警**：数据库连接数达到上限时告警
3. **任务失败告警**：质量检查任务失败时告警
4. **质量下降告警**：数据质量得分低于阈值时告警