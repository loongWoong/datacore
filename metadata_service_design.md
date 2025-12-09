# 元数据服务详细设计方案

## 1. 概述

元数据服务是数据中台的核心基础服务之一，负责管理所有数据资产的元数据信息，包括表结构信息、字段信息、业务属性等。该服务基于Spring Boot框架构建，使用MySQL作为主存储，Redis作为缓存层，提供高性能的元数据查询和管理功能。

## 2. 技术架构

### 2.1 技术选型
- **框架**：Spring Boot 3.x + Spring Cloud Alibaba
- **ORM**：MyBatis Plus
- **数据库**：MySQL 8.x
- **缓存**：Redis 7.x
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

### 2.2 服务架构图
```
┌─────────────────────────────────────────────────────────────┐
│                    元数据服务 API Layer                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 表管理   │  │ 字段管理 │  │ 版本管理 │  │ 查询接口 │   │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 表服务   │  │ 字段服务 │  │ 版本服务 │  │ 查询服务 │   │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 表仓储   │  │ 字段仓储 │  │ 版本仓储 │  │ 查询仓储 │   │
│  │ Mapper   │  │ Mapper   │  │ Mapper   │  │ Mapper   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │ MySQL    │  │ Redis    │  │ Elasticsearch              │
│  │ (主存储)  │  │ (缓存)    │  │ (搜索)                     │
│  └──────────┘  └──────────┘  └──────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## 3. 数据库设计

### 3.1 表结构设计

#### 3.1.1 元数据表信息表 (metadata_table_info)
```sql
CREATE TABLE metadata_table_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    schema_name VARCHAR(128) NOT NULL COMMENT '模式名称',
    table_name VARCHAR(128) NOT NULL COMMENT '表名',
    table_name_cn VARCHAR(256) COMMENT '中文表名',
    table_description TEXT COMMENT '表描述',
    business_domain VARCHAR(128) COMMENT '业务域',
    data_source VARCHAR(128) COMMENT '数据源',
    update_frequency VARCHAR(64) COMMENT '更新频率',
    owner VARCHAR(128) COMMENT '负责人/部门',
    data_layer VARCHAR(64) COMMENT '数据分层',
    record_count BIGINT DEFAULT 0 COMMENT '记录数',
    last_update_time DATETIME COMMENT '最后更新时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_schema_table (schema_name, table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表信息元数据表';
```

#### 3.1.2 元数据字段信息表 (metadata_column_info)
```sql
CREATE TABLE metadata_column_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    table_id BIGINT NOT NULL COMMENT '表ID',
    column_name VARCHAR(128) NOT NULL COMMENT '字段名',
    column_name_cn VARCHAR(256) COMMENT '中文字段名',
    column_description TEXT COMMENT '字段描述',
    data_type VARCHAR(64) COMMENT '数据类型',
    is_primary_key TINYINT(1) DEFAULT 0 COMMENT '是否主键',
    is_nullable TINYINT(1) DEFAULT 1 COMMENT '是否可为空',
    column_order INT DEFAULT 0 COMMENT '字段顺序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_table_column (table_id, column_name),
    INDEX idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段信息元数据表';
```

#### 3.1.3 元数据版本信息表 (metadata_version_info)
```sql
CREATE TABLE metadata_version_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    table_id BIGINT NOT NULL COMMENT '表ID',
    version_number VARCHAR(64) NOT NULL COMMENT '版本号',
    version_description TEXT COMMENT '版本描述',
    metadata_content JSON COMMENT '元数据内容',
    created_by VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_table_version (table_id, version_number),
    INDEX idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据版本信息表';
```

## 4. 核心功能实现

### 4.1 表管理功能

#### 4.1.1 表信息查询
```java
@RestController
@RequestMapping("/api/metadata/tables")
public class TableController {
    
    @Autowired
    private TableService tableService;
    
    /**
     * 获取表列表
     */
    @GetMapping
    public ResponseEntity<List<TableInfoDTO>> getTables(
            @RequestParam(required = false) String layer,
            @RequestParam(required = false) String businessDomain,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String search) {
        
        TableQueryDTO queryDTO = TableQueryDTO.builder()
                .layer(layer)
                .businessDomain(businessDomain)
                .owner(owner)
                .search(search)
                .build();
                
        List<TableInfoDTO> tables = tableService.getTables(queryDTO);
        return ResponseEntity.ok(tables);
    }
    
    /**
     * 获取表详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<TableDetailDTO> getTableDetail(@PathVariable Long id) {
        TableDetailDTO tableDetail = tableService.getTableDetail(id);
        return ResponseEntity.ok(tableDetail);
    }
}
```

#### 4.1.2 表信息管理
```java
@Service
public class TableServiceImpl implements TableService {
    
    @Autowired
    private TableInfoMapper tableInfoMapper;
    
    @Autowired
    private ColumnInfoMapper columnInfoMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public TableInfoDTO createTable(CreateTableDTO createDTO) {
        // 1. 创建表信息
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(createDTO, tableInfo);
        tableInfo.setCreatedTime(new Date());
        tableInfo.setUpdatedTime(new Date());
        tableInfoMapper.insert(tableInfo);
        
        // 2. 创建字段信息
        if (createDTO.getColumns() != null) {
            for (CreateColumnDTO columnDTO : createDTO.getColumns()) {
                ColumnInfo columnInfo = new ColumnInfo();
                BeanUtils.copyProperties(columnDTO, columnInfo);
                columnInfo.setTableId(tableInfo.getId());
                columnInfo.setCreatedTime(new Date());
                columnInfo.setUpdatedTime(new Date());
                columnInfoMapper.insert(columnInfo);
            }
        }
        
        // 3. 清除缓存
        redisTemplate.delete("table:list");
        
        // 4. 返回结果
        TableInfoDTO result = new TableInfoDTO();
        BeanUtils.copyProperties(tableInfo, result);
        return result;
    }
    
    @Override
    public List<TableInfoDTO> getTables(TableQueryDTO queryDTO) {
        // 1. 尝试从缓存获取
        String cacheKey = "table:list:" + JSON.toJSONString(queryDTO);
        List<TableInfoDTO> cachedResult = (List<TableInfoDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        List<TableInfo> tableInfos = tableInfoMapper.selectByCondition(queryDTO);
        List<TableInfoDTO> result = tableInfos.stream()
                .map(tableInfo -> {
                    TableInfoDTO dto = new TableInfoDTO();
                    BeanUtils.copyProperties(tableInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
}
```

### 4.2 字段管理功能

#### 4.2.1 字段信息查询
```java
@RestController
@RequestMapping("/api/metadata/columns")
public class ColumnController {
    
    @Autowired
    private ColumnService columnService;
    
    /**
     * 获取表的字段列表
     */
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<ColumnInfoDTO>> getColumnsByTable(@PathVariable Long tableId) {
        List<ColumnInfoDTO> columns = columnService.getColumnsByTable(tableId);
        return ResponseEntity.ok(columns);
    }
}
```

#### 4.2.2 字段信息管理
```java
@Service
public class ColumnServiceImpl implements ColumnService {
    
    @Autowired
    private ColumnInfoMapper columnInfoMapper;
    
    @Override
    @Transactional
    public ColumnInfoDTO createColumn(CreateColumnDTO createDTO) {
        ColumnInfo columnInfo = new ColumnInfo();
        BeanUtils.copyProperties(createDTO, columnInfo);
        columnInfo.setCreatedTime(new Date());
        columnInfo.setUpdatedTime(new Date());
        columnInfoMapper.insert(columnInfo);
        
        ColumnInfoDTO result = new ColumnInfoDTO();
        BeanUtils.copyProperties(columnInfo, result);
        return result;
    }
    
    @Override
    public List<ColumnInfoDTO> getColumnsByTable(Long tableId) {
        List<ColumnInfo> columnInfos = columnInfoMapper.selectByTableId(tableId);
        return columnInfos.stream()
                .map(columnInfo -> {
                    ColumnInfoDTO dto = new ColumnInfoDTO();
                    BeanUtils.copyProperties(columnInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
```

### 4.3 版本管理功能

#### 4.3.1 版本信息管理
```java
@Service
public class VersionServiceImpl implements VersionService {
    
    @Autowired
    private VersionInfoMapper versionInfoMapper;
    
    @Autowired
    private TableInfoMapper tableInfoMapper;
    
    @Override
    @Transactional
    public VersionInfoDTO createVersion(CreateVersionDTO createDTO) {
        // 1. 获取当前表信息
        TableInfo tableInfo = tableInfoMapper.selectById(createDTO.getTableId());
        if (tableInfo == null) {
            throw new BusinessException("表不存在");
        }
        
        // 2. 构造元数据内容
        JSONObject metadataContent = new JSONObject();
        metadataContent.put("tableInfo", tableInfo);
        
        // 3. 创建版本信息
        VersionInfo versionInfo = new VersionInfo();
        BeanUtils.copyProperties(createDTO, versionInfo);
        versionInfo.setMetadataContent(metadataContent.toJSONString());
        versionInfo.setCreatedBy(SecurityUtils.getCurrentUsername());
        versionInfo.setCreatedTime(new Date());
        versionInfoMapper.insert(versionInfo);
        
        VersionInfoDTO result = new VersionInfoDTO();
        BeanUtils.copyProperties(versionInfo, result);
        return result;
    }
    
    @Override
    public List<VersionInfoDTO> getVersionsByTable(Long tableId) {
        List<VersionInfo> versionInfos = versionInfoMapper.selectByTableId(tableId);
        return versionInfos.stream()
                .map(versionInfo -> {
                    VersionInfoDTO dto = new VersionInfoDTO();
                    BeanUtils.copyProperties(versionInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
```

## 5. 接口设计

### 5.1 表管理接口

#### 5.1.1 获取表列表
- **接口地址**：GET /api/metadata/tables
- **请求参数**：
  - layer (String, optional)：数据分层
  - businessDomain (String, optional)：业务域
  - owner (String, optional)：负责人
  - search (String, optional)：搜索关键字
- **响应示例**：
```json
[
  {
    "id": 1,
    "schemaName": "main_dwd",
    "tableName": "dwd_toll_transaction_detail",
    "tableNameCn": "收费交易明细表",
    "tableDescription": "收费交易明细数据",
    "businessDomain": "收费业务",
    "dataSource": "收费系统",
    "updateFrequency": "实时",
    "owner": "收费中心",
    "dataLayer": "DWD",
    "recordCount": 1000000,
    "lastUpdateTime": "2025-12-09T10:30:00"
  }
]
```

#### 5.1.2 获取表详情
- **接口地址**：GET /api/metadata/tables/{id}
- **路径参数**：
  - id (Long)：表ID
- **响应示例**：
```json
{
  "id": 1,
  "schemaName": "main_dwd",
  "tableName": "dwd_toll_transaction_detail",
  "tableNameCn": "收费交易明细表",
  "tableDescription": "收费交易明细数据",
  "businessDomain": "收费业务",
  "dataSource": "收费系统",
  "updateFrequency": "实时",
  "owner": "收费中心",
  "dataLayer": "DWD",
  "recordCount": 1000000,
  "lastUpdateTime": "2025-12-09T10:30:00",
  "columns": [
    {
      "id": 1,
      "columnName": "transaction_id",
      "columnNameCn": "交易ID",
      "columnDescription": "交易唯一标识",
      "dataType": "VARCHAR(64)",
      "isPrimaryKey": true,
      "isNullable": false,
      "columnOrder": 1
    }
  ]
}
```

#### 5.1.3 创建表元数据
- **接口地址**：POST /api/metadata/tables
- **请求体**：
```json
{
  "schemaName": "main_dwd",
  "tableName": "dwd_new_table",
  "tableNameCn": "新表",
  "tableDescription": "新表描述",
  "businessDomain": "新业务",
  "dataSource": "新系统",
  "updateFrequency": "日",
  "owner": "新部门",
  "dataLayer": "DWD",
  "columns": [
    {
      "columnName": "id",
      "columnNameCn": "主键",
      "columnDescription": "主键字段",
      "dataType": "BIGINT",
      "isPrimaryKey": true,
      "isNullable": false,
      "columnOrder": 1
    }
  ]
}
```

### 5.2 字段管理接口

#### 5.2.1 获取表的字段列表
- **接口地址**：GET /api/metadata/columns/table/{tableId}
- **路径参数**：
  - tableId (Long)：表ID
- **响应示例**：
```json
[
  {
    "id": 1,
    "tableId": 1,
    "columnName": "transaction_id",
    "columnNameCn": "交易ID",
    "columnDescription": "交易唯一标识",
    "dataType": "VARCHAR(64)",
    "isPrimaryKey": true,
    "isNullable": false,
    "columnOrder": 1
  }
]
```

### 5.3 版本管理接口

#### 5.3.1 获取表的版本列表
- **接口地址**：GET /api/metadata/versions/table/{tableId}
- **路径参数**：
  - tableId (Long)：表ID
- **响应示例**：
```json
[
  {
    "id": 1,
    "tableId": 1,
    "versionNumber": "v1.0.0",
    "versionDescription": "初始版本",
    "createdBy": "admin",
    "createdTime": "2025-12-09T10:30:00"
  }
]
```

## 6. 性能优化措施

### 6.1 缓存策略
1. **表列表缓存**：将常用的表查询结果缓存到Redis中，设置10分钟过期时间
2. **表详情缓存**：将表详情信息缓存到Redis中，设置30分钟过期时间
3. **热点数据缓存**：对频繁访问的元数据进行缓存预热

### 6.2 数据库优化
1. **索引优化**：在常用查询字段上建立合适的索引
2. **分页查询**：对于大数据量的查询采用分页处理
3. **读写分离**：配置主从数据库，读操作走从库，写操作走主库

### 6.3 接口优化
1. **批量操作**：提供批量创建、更新接口，减少网络开销
2. **异步处理**：对于耗时操作采用异步处理方式
3. **压缩传输**：对大数据量的响应启用GZIP压缩

## 7. 安全设计

### 7.1 认证授权
1. **JWT Token**：使用JWT进行用户身份认证
2. **RBAC权限控制**：基于角色的访问控制，不同角色具有不同的操作权限
3. **接口权限校验**：每个接口都需要进行权限校验

### 7.2 数据安全
1. **敏感数据脱敏**：对敏感字段进行脱敏处理
2. **操作日志记录**：记录所有元数据变更操作日志
3. **数据备份**：定期对元数据进行备份

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
│  │ Metadata  │  │ │  │ Metadata  │  │ │  │ Metadata  │  │
│  │ Service   │  │ │  │ Service   │  │ │  │ Service   │  │
│  └───────────┘  │ │  └───────────┘  │ │  └───────────┘  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                    Shared Storage                           │
│  ┌───────────┐  ┌───────────┐  ┌─────────────────────────┐ │
│  │   MySQL   │  │   Redis   │  │   Elasticsearch         │ │
│  │ (主存储)   │  │ (缓存)     │  │ (搜索)                   │ │
│  └───────────┘  └───────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 8.2 部署步骤
1. **环境准备**：安装JDK 17、MySQL、Redis、Elasticsearch
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
4. **JVM内存使用**：监控服务内存使用情况

### 9.2 日志管理
1. **操作日志**：记录所有用户操作日志
2. **错误日志**：记录系统错误和异常信息
3. **性能日志**：记录关键接口的性能数据
4. **审计日志**：记录元数据变更的审计信息

### 9.3 告警机制
1. **接口超时告警**：接口响应时间超过阈值时告警
2. **数据库连接告警**：数据库连接数达到上限时告警
3. **缓存异常告警**：Redis连接异常或命中率过低时告警
4. **系统资源告警**：CPU、内存使用率过高时告警