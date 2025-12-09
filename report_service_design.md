# 报表服务详细设计方案

## 1. 概述

报表服务是数据中台的重要组成部分，负责各类业务报表的生成、管理和展示。该服务基于Spring Boot框架构建，集成了Apache POI用于Excel报表生成，使用JasperReports用于复杂报表模板处理，MySQL作为配置和元数据存储，Redis作为缓存层，提供全面的报表服务功能。

## 2. 技术架构

### 2.1 技术选型
- **框架**：Spring Boot 3.x + Spring Cloud Alibaba
- **报表引擎**：Apache POI 5.x、JasperReports 6.x
- **数据库**：MySQL 8.x
- **缓存**：Redis 7.x
- **文件存储**：MinIO或本地文件系统
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

### 2.2 服务架构图
```
┌─────────────────────────────────────────────────────────────┐
│                   Report Service API Layer                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 配置管理 │  │ 报表生成 │  │ 报表查询 │  │ 文件管理 │   │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 配置服务 │  │ 生成服务 │  │ 查询服务 │  │ 文件服务 │   │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 配置仓储 │  │ 生成仓储 │  │ 查询仓储 │  │ 文件仓储 │   │
│  │ Repository│  │ Repository│  │ Repository│  │ Repository│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ MySQL    │  │ Redis    │  │ MinIO    │  │ Local FS │   │
│  │ (配置)    │  │ (缓存)    │  │ (文件存储) │  │ (文件存储) │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 3. 数据库设计

### 3.1 MySQL表结构设计

#### 3.1.1 报表配置表 (report_config)
```sql
CREATE TABLE report_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_code VARCHAR(128) NOT NULL COMMENT '报表编码',
    report_name VARCHAR(256) NOT NULL COMMENT '报表名称',
    report_description TEXT COMMENT '报表描述',
    report_type VARCHAR(64) NOT NULL COMMENT '报表类型（SIMPLE/TEMPLATE/DASHBOARD）',
    data_source_type VARCHAR(64) NOT NULL COMMENT '数据源类型（SQL/API）',
    data_source_config TEXT COMMENT '数据源配置（JSON格式）',
    template_file_path VARCHAR(512) COMMENT '模板文件路径',
    output_format VARCHAR(64) DEFAULT 'EXCEL' COMMENT '输出格式（EXCEL/PDF/HTML/CSV）',
    schedule_config TEXT COMMENT '调度配置（JSON格式）',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_report_code (report_code),
    INDEX idx_report_type (report_type),
    INDEX idx_is_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表配置表';
```

#### 3.1.2 报表参数表 (report_parameter)
```sql
CREATE TABLE report_parameter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_id BIGINT NOT NULL COMMENT '报表ID',
    param_name VARCHAR(128) NOT NULL COMMENT '参数名称',
    param_label VARCHAR(256) NOT NULL COMMENT '参数标签',
    param_type VARCHAR(64) NOT NULL COMMENT '参数类型（STRING/INTEGER/DATE/BOOLEAN）',
    is_required TINYINT(1) DEFAULT 0 COMMENT '是否必填',
    default_value VARCHAR(512) COMMENT '默认值',
    param_options TEXT COMMENT '参数选项（JSON格式，用于下拉框等）',
    param_order INT DEFAULT 0 COMMENT '参数顺序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_report_param (report_id, param_name),
    INDEX idx_report_id (report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表参数表';
```

#### 3.1.3 报表生成记录表 (report_generation_record)
```sql
CREATE TABLE report_generation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_id BIGINT NOT NULL COMMENT '报表ID',
    report_code VARCHAR(128) NOT NULL COMMENT '报表编码',
    report_name VARCHAR(256) NOT NULL COMMENT '报表名称',
    generation_status VARCHAR(32) NOT NULL COMMENT '生成状态（PENDING/RUNNING/SUCCESS/FAILED）',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    execution_time BIGINT COMMENT '执行耗时（毫秒）',
    file_path VARCHAR(512) COMMENT '生成文件路径',
    file_size BIGINT COMMENT '文件大小（字节）',
    error_message TEXT COMMENT '错误信息',
    generated_by VARCHAR(128) COMMENT '生成人',
    generated_params TEXT COMMENT '生成参数（JSON格式）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_report_id (report_id),
    INDEX idx_generation_status (generation_status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表生成记录表';
```

#### 3.1.4 报表订阅表 (report_subscription)
```sql
CREATE TABLE report_subscription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_id BIGINT NOT NULL COMMENT '报表ID',
    subscriber VARCHAR(128) NOT NULL COMMENT '订阅人',
    subscription_type VARCHAR(64) NOT NULL COMMENT '订阅类型（EMAIL/WEBHOOK）',
    subscription_config TEXT NOT NULL COMMENT '订阅配置（JSON格式）',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否激活',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_report_subscriber (report_id, subscriber),
    INDEX idx_report_id (report_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表订阅表';
```

## 4. 核心功能实现

### 4.1 报表配置管理

#### 4.1.1 报表配置接口
```java
@RestController
@RequestMapping("/api/reports/config")
public class ReportConfigController {
    
    @Autowired
    private ReportConfigService reportConfigService;
    
    /**
     * 创建报表配置
     */
    @PostMapping
    public ResponseEntity<ReportConfigDTO> createReportConfig(@RequestBody CreateReportConfigDTO createDTO) {
        ReportConfigDTO config = reportConfigService.createReportConfig(createDTO);
        return ResponseEntity.ok(config);
    }
    
    /**
     * 获取报表配置列表
     */
    @GetMapping
    public ResponseEntity<PageResult<ReportConfigDTO>> getReportConfigs(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String reportName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        ReportConfigQuery query = ReportConfigQuery.builder()
                .reportType(reportType)
                .reportName(reportName)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<ReportConfigDTO> pageResult = reportConfigService.getReportConfigs(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取报表配置详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportConfigDetailDTO> getReportConfigDetail(@PathVariable Long id) {
        ReportConfigDetailDTO detail = reportConfigService.getReportConfigDetail(id);
        return ResponseEntity.ok(detail);
    }
    
    /**
     * 更新报表配置
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportConfigDTO> updateReportConfig(@PathVariable Long id, @RequestBody UpdateReportConfigDTO updateDTO) {
        ReportConfigDTO config = reportConfigService.updateReportConfig(id, updateDTO);
        return ResponseEntity.ok(config);
    }
    
    /**
     * 删除报表配置
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReportConfig(@PathVariable Long id) {
        reportConfigService.deleteReportConfig(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### 4.1.2 报表配置服务实现
```java
@Service
public class ReportConfigServiceImpl implements ReportConfigService {
    
    @Autowired
    private ReportConfigMapper reportConfigMapper;
    
    @Autowired
    private ReportParameterMapper reportParameterMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public ReportConfigDTO createReportConfig(CreateReportConfigDTO createDTO) {
        // 1. 校验报表编码唯一性
        ReportConfig existingConfig = reportConfigMapper.selectByReportCode(createDTO.getReportCode());
        if (existingConfig != null) {
            throw new BusinessException("报表编码已存在");
        }
        
        // 2. 创建报表配置
        ReportConfig config = new ReportConfig();
        BeanUtils.copyProperties(createDTO, config);
        config.setCreator(SecurityUtils.getCurrentUsername());
        config.setCreatedTime(new Date());
        config.setUpdatedTime(new Date());
        reportConfigMapper.insert(config);
        
        // 3. 创建报表参数
        if (createDTO.getParameters() != null) {
            for (CreateReportParameterDTO paramDTO : createDTO.getParameters()) {
                ReportParameter parameter = new ReportParameter();
                BeanUtils.copyProperties(paramDTO, parameter);
                parameter.setReportId(config.getId());
                parameter.setCreatedTime(new Date());
                reportParameterMapper.insert(parameter);
            }
        }
        
        // 4. 清除缓存
        redisTemplate.delete("report:configs");
        
        // 5. 返回结果
        ReportConfigDTO result = new ReportConfigDTO();
        BeanUtils.copyProperties(config, result);
        return result;
    }
    
    @Override
    public PageResult<ReportConfigDTO> getReportConfigs(ReportConfigQuery query) {
        // 1. 尝试从缓存获取
        String cacheKey = "report:configs:" + JSON.toJSONString(query);
        PageResult<ReportConfigDTO> cachedResult = (PageResult<ReportConfigDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ReportConfig> configs = reportConfigMapper.selectByCondition(query);
        
        List<ReportConfigDTO> dtos = configs.stream()
                .map(config -> {
                    ReportConfigDTO dto = new ReportConfigDTO();
                    BeanUtils.copyProperties(config, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<ReportConfig> pageInfo = new PageInfo<>(configs);
        PageResult<ReportConfigDTO> result = PageResult.of(dtos, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
    
    @Override
    public ReportConfigDetailDTO getReportConfigDetail(Long id) {
        // 1. 查询报表配置
        ReportConfig config = reportConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException("报表配置不存在");
        }
        
        // 2. 查询报表参数
        List<ReportParameter> parameters = reportParameterMapper.selectByReportId(id);
        
        // 3. 构造详细信息DTO
        ReportConfigDetailDTO detailDTO = new ReportConfigDetailDTO();
        BeanUtils.copyProperties(config, detailDTO);
        
        List<ReportParameterDTO> parameterDTOs = parameters.stream()
                .map(param -> {
                    ReportParameterDTO paramDTO = new ReportParameterDTO();
                    BeanUtils.copyProperties(param, paramDTO);
                    return paramDTO;
                })
                .collect(Collectors.toList());
                
        detailDTO.setParameters(parameterDTOs);
        
        return detailDTO;
    }
    
    @Override
    @Transactional
    public ReportConfigDTO updateReportConfig(Long id, UpdateReportConfigDTO updateDTO) {
        ReportConfig existingConfig = reportConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new BusinessException("报表配置不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingConfig);
        existingConfig.setUpdatedTime(new Date());
        reportConfigMapper.updateById(existingConfig);
        
        // 更新参数
        if (updateDTO.getParameters() != null) {
            // 先删除原有参数
            reportParameterMapper.deleteByReportId(id);
            
            // 再插入新参数
            for (UpdateReportParameterDTO paramDTO : updateDTO.getParameters()) {
                ReportParameter parameter = new ReportParameter();
                BeanUtils.copyProperties(paramDTO, parameter);
                parameter.setReportId(id);
                parameter.setCreatedTime(new Date());
                reportParameterMapper.insert(parameter);
            }
        }
        
        // 清除缓存
        redisTemplate.delete("report:configs");
        redisTemplate.delete("report:config:" + id);
        
        ReportConfigDTO result = new ReportConfigDTO();
        BeanUtils.copyProperties(existingConfig, result);
        return result;
    }
    
    @Override
    @Transactional
    public void deleteReportConfig(Long id) {
        ReportConfig existingConfig = reportConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new BusinessException("报表配置不存在");
        }
        
        // 删除相关参数
        reportParameterMapper.deleteByReportId(id);
        
        // 删除配置
        reportConfigMapper.deleteById(id);
        
        // 清除缓存
        redisTemplate.delete("report:configs");
        redisTemplate.delete("report:config:" + id);
    }
}
```

### 4.2 报表生成服务

#### 4.2.1 报表生成接口
```java
@RestController
@RequestMapping("/api/reports/generate")
public class ReportGenerationController {
    
    @Autowired
    private ReportGenerationService reportGenerationService;
    
    /**
     * 生成报表
     */
    @PostMapping
    public ResponseEntity<ReportGenerationResultDTO> generateReport(@RequestBody GenerateReportDTO generateDTO) {
        ReportGenerationResultDTO result = reportGenerationService.generateReport(generateDTO);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取生成记录列表
     */
    @GetMapping("/records")
    public ResponseEntity<PageResult<ReportGenerationRecordDTO>> getGenerationRecords(
            @RequestParam(required = false) Long reportId,
            @RequestParam(required = false) String generationStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        GenerationRecordQuery query = GenerationRecordQuery.builder()
                .reportId(reportId)
                .generationStatus(generationStatus)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<ReportGenerationRecordDTO> pageResult = reportGenerationService.getGenerationRecords(query);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 下载报表文件
     */
    @GetMapping("/download/{recordId}")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long recordId) {
        ReportFileResource fileResource = reportGenerationService.downloadReport(recordId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(fileResource.getContentType()))
                .contentLength(fileResource.getFileSize())
                .body(fileResource.getResource());
    }
}
```

#### 4.2.2 报表生成服务实现
```java
@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {
    
    @Autowired
    private ReportConfigMapper reportConfigMapper;
    
    @Autowired
    private ReportParameterMapper reportParameterMapper;
    
    @Autowired
    private ReportGenerationRecordMapper reportGenerationRecordMapper;
    
    @Autowired
    private ReportGeneratorFactory reportGeneratorFactory;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Override
    @Async
    public ReportGenerationResultDTO generateReport(GenerateReportDTO generateDTO) {
        // 1. 查询报表配置
        ReportConfig config = reportConfigMapper.selectById(generateDTO.getReportId());
        if (config == null) {
            throw new BusinessException("报表配置不存在");
        }
        
        // 2. 创建生成记录
        ReportGenerationRecord record = new ReportGenerationRecord();
        record.setReportId(config.getId());
        record.setReportCode(config.getReportCode());
        record.setReportName(config.getReportName());
        record.setGenerationStatus("PENDING");
        record.setGeneratedBy(SecurityUtils.getCurrentUsername());
        record.setGeneratedParams(JSON.toJSONString(generateDTO.getParameters()));
        record.setCreatedTime(new Date());
        reportGenerationRecordMapper.insert(record);
        
        try {
            // 3. 更新状态为RUNNING
            record.setGenerationStatus("RUNNING");
            record.setStartTime(new Date());
            reportGenerationRecordMapper.updateById(record);
            
            // 4. 参数校验和处理
            validateAndProcessParameters(config.getId(), generateDTO.getParameters());
            
            // 5. 生成报表
            ReportGenerationContext context = ReportGenerationContext.builder()
                    .reportConfig(config)
                    .parameters(generateDTO.getParameters())
                    .build();
                    
            ReportGenerator generator = reportGeneratorFactory.getGenerator(config.getReportType());
            ReportGenerationResult generationResult = generator.generate(context);
            
            // 6. 保存文件
            String filePath = fileStorageService.saveFile(generationResult.getFileContent(), 
                generationResult.getFileName());
            
            // 7. 更新生成记录
            record.setGenerationStatus("SUCCESS");
            record.setEndTime(new Date());
            record.setExecutionTime(System.currentTimeMillis() - record.getStartTime().getTime());
            record.setFilePath(filePath);
            record.setFileSize(generationResult.getFileSize());
            reportGenerationRecordMapper.updateById(record);
            
            // 8. 返回结果
            ReportGenerationResultDTO result = new ReportGenerationResultDTO();
            result.setSuccess(true);
            result.setRecordId(record.getId());
            result.setFileName(generationResult.getFileName());
            result.setFileSize(generationResult.getFileSize());
            return result;
            
        } catch (Exception e) {
            // 更新生成记录为失败状态
            record.setGenerationStatus("FAILED");
            record.setEndTime(new Date());
            record.setErrorMessage(e.getMessage());
            reportGenerationRecordMapper.updateById(record);
            
            throw new BusinessException("报表生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<ReportGenerationRecordDTO> getGenerationRecords(GenerationRecordQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ReportGenerationRecord> records = reportGenerationRecordMapper.selectByCondition(query);
        
        List<ReportGenerationRecordDTO> dtos = records.stream()
                .map(record -> {
                    ReportGenerationRecordDTO dto = new ReportGenerationRecordDTO();
                    BeanUtils.copyProperties(record, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<ReportGenerationRecord> pageInfo = new PageInfo<>(records);
        return PageResult.of(dtos, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }
    
    @Override
    public ReportFileResource downloadReport(Long recordId) {
        ReportGenerationRecord record = reportGenerationRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("生成记录不存在");
        }
        
        if (!"SUCCESS".equals(record.getGenerationStatus())) {
            throw new BusinessException("报表生成未成功");
        }
        
        // 从文件存储服务获取文件
        return fileStorageService.getFileResource(record.getFilePath());
    }
    
    /**
     * 参数校验和处理
     */
    private void validateAndProcessParameters(Long reportId, Map<String, Object> parameters) {
        List<ReportParameter> reportParameters = reportParameterMapper.selectByReportId(reportId);
        
        for (ReportParameter param : reportParameters) {
            Object value = parameters.get(param.getParamName());
            
            // 必填参数校验
            if (param.getIsRequired() && (value == null || StringUtils.isEmpty(value.toString()))) {
                throw new BusinessException("参数 " + param.getParamLabel() + " 不能为空");
            }
            
            // 默认值处理
            if (value == null && StringUtils.isNotEmpty(param.getDefaultValue())) {
                parameters.put(param.getParamName(), param.getDefaultValue());
            }
            
            // 类型转换
            if (value != null) {
                parameters.put(param.getParamName(), convertParameterValue(value, param.getParamType()));
            }
        }
    }
    
    /**
     * 参数值类型转换
     */
    private Object convertParameterValue(Object value, String paramType) {
        switch (paramType.toUpperCase()) {
            case "INTEGER":
                return Integer.valueOf(value.toString());
            case "DATE":
                return DateUtils.parseDate(value.toString());
            case "BOOLEAN":
                return Boolean.valueOf(value.toString());
            default:
                return value.toString();
        }
    }
}
```

### 4.3 报表生成器实现

#### 4.3.1 简单报表生成器（Excel）
```java
@Component
public class SimpleExcelReportGenerator implements ReportGenerator {
    
    @Override
    public boolean supports(String reportType) {
        return "SIMPLE".equals(reportType);
    }
    
    @Override
    public ReportGenerationResult generate(ReportGenerationContext context) {
        ReportConfig config = context.getReportConfig();
        Map<String, Object> parameters = context.getParameters();
        
        // 1. 执行数据查询
        List<Map<String, Object>> data = executeQuery(config.getDataSourceConfig(), parameters);
        
        // 2. 生成Excel文件
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("报表数据");
        
        // 创建表头
        if (!data.isEmpty()) {
            Row headerRow = sheet.createRow(0);
            Map<String, Object> firstRow = data.get(0);
            int cellIndex = 0;
            for (String columnName : firstRow.keySet()) {
                Cell cell = headerRow.createCell(cellIndex++);
                cell.setCellValue(columnName);
            }
            
            // 填充数据
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> rowData = data.get(i);
                cellIndex = 0;
                for (Object value : rowData.values()) {
                    Cell cell = row.createCell(cellIndex++);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
                }
            }
        }
        
        // 3. 写入字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new BusinessException("生成Excel文件失败", e);
        }
        
        // 4. 构造结果
        String fileName = config.getReportName() + "_" + System.currentTimeMillis() + ".xlsx";
        return ReportGenerationResult.builder()
                .fileName(fileName)
                .fileSize(outputStream.size())
                .fileContent(outputStream.toByteArray())
                .build();
    }
    
    /**
     * 执行数据查询
     */
    private List<Map<String, Object>> executeQuery(String dataSourceConfig, Map<String, Object> parameters) {
        DataSourceConfig config = JSON.parseObject(dataSourceConfig, DataSourceConfig.class);
        
        // 替换SQL中的参数占位符
        String sql = replaceParameters(config.getSql(), parameters);
        
        // 执行查询
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 替换SQL参数
     */
    private String replaceParameters(String sql, Map<String, Object> parameters) {
        String result = sql;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String placeholder = "#{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, "'" + value + "'");
        }
        return result;
    }
}
```

#### 4.3.2 模板报表生成器（JasperReports）
```java
@Component
public class TemplateReportGenerator implements ReportGenerator {
    
    @Autowired
    private JasperReportCompiler jasperReportCompiler;
    
    @Override
    public boolean supports(String reportType) {
        return "TEMPLATE".equals(reportType);
    }
    
    @Override
    public ReportGenerationResult generate(ReportGenerationContext context) {
        ReportConfig config = context.getReportConfig();
        Map<String, Object> parameters = context.getParameters();
        
        try {
            // 1. 编译报表模板
            JasperReport jasperReport = jasperReportCompiler.compile(config.getTemplateFilePath());
            
            // 2. 准备数据源
            JRDataSource dataSource = prepareDataSource(config.getDataSourceConfig(), parameters);
            
            // 3. 填充报表
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // 4. 导出报表
            byte[] reportBytes;
            String fileExtension;
            
            switch (config.getOutputFormat().toUpperCase()) {
                case "PDF":
                    reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);
                    fileExtension = ".pdf";
                    break;
                case "HTML":
                    reportBytes = JasperExportManager.exportReportToHtml(jasperPrint).getBytes();
                    fileExtension = ".html";
                    break;
                case "CSV":
                    JRCsvExporter csvExporter = new JRCsvExporter();
                    ByteArrayOutputStream csvOutputStream = new ByteArrayOutputStream();
                    csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    csvExporter.setExporterOutput(new SimpleWriterExporterOutput(csvOutputStream));
                    csvExporter.exportReport();
                    reportBytes = csvOutputStream.toByteArray();
                    fileExtension = ".csv";
                    break;
                default:
                    // 默认导出为Excel
                    JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                    ByteArrayOutputStream excelOutputStream = new ByteArrayOutputStream();
                    xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(excelOutputStream));
                    xlsxExporter.exportReport();
                    reportBytes = excelOutputStream.toByteArray();
                    fileExtension = ".xlsx";
                    break;
            }
            
            // 5. 构造结果
            String fileName = config.getReportName() + "_" + System.currentTimeMillis() + fileExtension;
            return ReportGenerationResult.builder()
                    .fileName(fileName)
                    .fileSize(reportBytes.length)
                    .fileContent(reportBytes)
                    .build();
                    
        } catch (Exception e) {
            throw new BusinessException("生成模板报表失败", e);
        }
    }
    
    /**
     * 准备数据源
     */
    private JRDataSource prepareDataSource(String dataSourceConfig, Map<String, Object> parameters) {
        DataSourceConfig config = JSON.parseObject(dataSourceConfig, DataSourceConfig.class);
        
        // 根据数据源类型准备不同的JRDataSource实现
        if ("SQL".equals(config.getType())) {
            // SQL数据源
            String sql = replaceParameters(config.getSql(), parameters);
            return new JRResultSetDataSource(jdbcTemplate.queryForRowSet(sql));
        } else if ("API".equals(config.getType())) {
            // API数据源
            List<Map<String, Object>> apiData = callApi(config.getApiUrl(), parameters);
            return new JRMapArrayDataSource(apiData.toArray(new Map[0]));
        } else {
            // 默认空数据源
            return new JREmptyDataSource();
        }
    }
    
    /**
     * 调用API获取数据
     */
    private List<Map<String, Object>> callApi(String apiUrl, Map<String, Object> parameters) {
        // 实现API调用逻辑
        return new ArrayList<>();
    }
}
```

## 5. 接口设计

### 5.1 报表配置接口

#### 5.1.1 创建报表配置
- **接口地址**：POST /api/reports/config
- **请求体**：
```json
{
  "reportCode": "REVENUE_DAILY_REPORT",
  "reportName": "日收费收入报表",
  "reportDescription": "按日期统计收费收入情况",
  "reportType": "SIMPLE",
  "dataSourceType": "SQL",
  "dataSourceConfig": "{\"type\":\"SQL\",\"sql\":\"SELECT transaction_date, total_amount FROM revenue_daily WHERE transaction_date BETWEEN #{startDate} AND #{endDate}\"}",
  "outputFormat": "EXCEL",
  "parameters": [
    {
      "paramName": "startDate",
      "paramLabel": "开始日期",
      "paramType": "DATE",
      "isRequired": true
    },
    {
      "paramName": "endDate",
      "paramLabel": "结束日期",
      "paramType": "DATE",
      "isRequired": true
    }
  ]
}
```

#### 5.1.2 获取报表配置列表
- **接口地址**：GET /api/reports/config
- **请求参数**：
  - reportType (String, optional)：报表类型
  - reportName (String, optional)：报表名称
  - pageNum (Integer, optional, default=1)：页码
  - pageSize (Integer, optional, default=10)：每页大小
- **响应示例**：
```json
{
  "data": [
    {
      "id": 1,
      "reportCode": "REVENUE_DAILY_REPORT",
      "reportName": "日收费收入报表",
      "reportDescription": "按日期统计收费收入情况",
      "reportType": "SIMPLE",
      "dataSourceType": "SQL",
      "outputFormat": "EXCEL",
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

### 5.2 报表生成接口

#### 5.2.1 生成报表
- **接口地址**：POST /api/reports/generate
- **请求体**：
```json
{
  "reportId": 1,
  "parameters": {
    "startDate": "2025-12-01",
    "endDate": "2025-12-09"
  }
}
```
- **响应示例**：
```json
{
  "success": true,
  "recordId": 1,
  "fileName": "日收费收入报表_1728489200000.xlsx",
  "fileSize": 102400
}
```

#### 5.2.2 获取生成记录列表
- **接口地址**：GET /api/reports/generate/records
- **请求参数**：
  - reportId (Long, optional)：报表ID
  - generationStatus (String, optional)：生成状态
  - pageNum (Integer, optional, default=1)：页码
  - pageSize (Integer, optional, default=10)：每页大小
- **响应示例**：
```json
{
  "data": [
    {
      "id": 1,
      "reportId": 1,
      "reportCode": "REVENUE_DAILY_REPORT",
      "reportName": "日收费收入报表",
      "generationStatus": "SUCCESS",
      "startTime": "2025-12-09T10:30:00",
      "endTime": "2025-12-09T10:30:05",
      "executionTime": 5000,
      "filePath": "/reports/日收费收入报表_1728489200000.xlsx",
      "fileSize": 102400,
      "generatedBy": "admin",
      "createdTime": "2025-12-09T10:30:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

#### 5.2.3 下载报表文件
- **接口地址**：GET /api/reports/generate/download/{recordId}
- **路径参数**：
  - recordId (Long)：生成记录ID
- **响应**：文件下载流

## 6. 性能优化措施

### 6.1 数据库优化
1. **索引优化**：在常用查询字段上建立合适的索引
2. **分页查询**：对于大数据量的查询采用分页处理
3. **读写分离**：配置主从数据库，读操作走从库，写操作走主库

### 6.2 缓存策略
1. **配置缓存**：将常用的报表配置缓存到Redis中
2. **生成记录缓存**：将近期的生成记录缓存到Redis中
3. **热点数据缓存**：对频繁访问的报表数据进行缓存预热

### 6.3 异步处理
1. **报表生成异步执行**：报表生成采用异步方式进行，避免阻塞接口响应
2. **文件存储异步处理**：大文件的存储操作采用异步处理
3. **批量操作**：提供批量生成报表的接口

## 7. 安全设计

### 7.1 认证授权
1. **JWT Token**：使用JWT进行用户身份认证
2. **RBAC权限控制**：基于角色的访问控制，不同角色具有不同的操作权限
3. **接口权限校验**：每个接口都需要进行权限校验

### 7.2 数据安全
1. **敏感数据脱敏**：对报表中的敏感数据进行脱敏处理
2. **操作日志记录**：记录所有报表生成和下载操作日志
3. **文件访问控制**：对生成的报表文件进行访问权限控制

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
│  │ Report    │  │ │  │ Report    │  │ │  │ Report    │  │
│  │ Service   │  │ │  │ Service   │  │ │  │ Service   │  │
│  └───────────┘  │ │  └───────────┘  │ │  └───────────┘  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                    Shared Storage                           │
│  ┌───────────┐  ┌───────────┐  ┌─────────────────────────┐ │
│  │   MySQL   │  │   Redis   │  │   MinIO                 │ │
│  │ (配置)     │  │ (缓存)     │  │ (文件存储)               │ │
│  └───────────┘  └───────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 8.2 部署步骤
1. **环境准备**：安装JDK 17、MySQL、Redis、MinIO
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
4. **报表生成成功率**：监控报表生成任务的成功率

### 9.2 日志管理
1. **操作日志**：记录所有用户操作日志
2. **错误日志**：记录系统错误和异常信息
3. **性能日志**：记录关键接口的性能数据
4. **审计日志**：记录报表生成和下载的审计信息

### 9.3 告警机制
1. **接口超时告警**：接口响应时间超过阈值时告警
2. **数据库连接告警**：数据库连接数达到上限时告警
3. **生成失败告警**：报表生成任务失败时告警
4. **文件存储告警**：文件存储空间不足时告警