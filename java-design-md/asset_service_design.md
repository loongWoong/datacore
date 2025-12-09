# 数据资产服务详细设计方案

## 1. 概述

数据资产服务是数据中台的核心服务之一，负责数据资产的目录管理、分类评估、价值分析和全生命周期管理。该服务基于Spring Boot框架构建，使用MySQL存储资产基本信息，Elasticsearch提供资产搜索能力，Redis作为缓存层，提供全面的数据资产管理功能。

## 2. 技术架构

### 2.1 技术选型
- **框架**：Spring Boot 3.x + Spring Cloud Alibaba
- **主存储**：MySQL 8.x
- **搜索引擎**：Elasticsearch 8.x
- **缓存**：Redis 7.x
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

### 2.2 服务架构图
```
┌─────────────────────────────────────────────────────────────┐
│                    Asset Service API Layer                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 目录管理 │  │ 分类评估 │  │ 搜索接口 │  │ 生命周期 │   │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 目录服务 │  │ 评估服务 │  │ 搜索服务 │  │ 生命周期服务 │   │
│  │ Service  │  │ Service  │  │ Service  │  │ Service    │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 目录仓储 │  │ 评估仓储 │  │ 搜索仓储 │  │ 生命周期仓储 │   │
│  │ Repository│  │ Repository│  │ Repository│  │ Repository │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ MySQL    │  │ Redis    │  │ Elasticsearch              │   │
│  │ (主存储)  │  │ (缓存)    │  │ (搜索)                   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 3. 数据库设计

### 3.1 MySQL表结构设计

#### 3.1.1 数据资产信息表 (asset_info)
```sql
CREATE TABLE asset_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    asset_code VARCHAR(128) NOT NULL COMMENT '资产编码',
    asset_name VARCHAR(256) NOT NULL COMMENT '资产名称',
    asset_name_cn VARCHAR(256) COMMENT '资产中文名称',
    asset_description TEXT COMMENT '资产描述',
    asset_type VARCHAR(64) NOT NULL COMMENT '资产类型（TABLE/FIELD/API等）',
    business_domain VARCHAR(128) COMMENT '业务域',
    data_source VARCHAR(128) COMMENT '数据源',
    owner VARCHAR(128) COMMENT '负责人',
    department VARCHAR(128) COMMENT '所属部门',
    schema_name VARCHAR(128) COMMENT '模式名称（表资产专用）',
    table_name VARCHAR(128) COMMENT '表名（表资产专用）',
    column_name VARCHAR(128) COMMENT '字段名（字段资产专用）',
    sensitivity_level VARCHAR(32) DEFAULT 'PUBLIC' COMMENT '敏感级别（PUBLIC/INTERNAL/CONFIDENTIAL/SECRET）',
    asset_status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '资产状态（ACTIVE/DEPRECATED/ARCHIVED）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_asset_code (asset_code),
    INDEX idx_asset_type (asset_type),
    INDEX idx_business_domain (business_domain),
    INDEX idx_owner (owner),
    INDEX idx_table_name (schema_name, table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据资产信息表';
```

#### 3.1.2 资产分类标签表 (asset_tag)
```sql
CREATE TABLE asset_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tag_code VARCHAR(128) NOT NULL COMMENT '标签编码',
    tag_name VARCHAR(128) NOT NULL COMMENT '标签名称',
    tag_description TEXT COMMENT '标签描述',
    tag_category VARCHAR(64) NOT NULL COMMENT '标签分类（业务标签/技术标签/安全标签等）',
    tag_color VARCHAR(32) COMMENT '标签颜色',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tag_code (tag_code),
    INDEX idx_tag_category (tag_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产标签表';
```

#### 3.1.3 资产标签关联表 (asset_tag_relation)
```sql
CREATE TABLE asset_tag_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_asset_tag (asset_id, tag_id),
    INDEX idx_asset_id (asset_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产标签关联表';
```

#### 3.1.4 资产评估指标表 (asset_evaluation)
```sql
CREATE TABLE asset_evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    evaluation_date DATE NOT NULL COMMENT '评估日期',
    asset_level VARCHAR(32) COMMENT '资产等级（CORE/APPLICATION/BASIC）',
    usage_frequency VARCHAR(32) COMMENT '使用频率（HIGH/MEDIUM/LOW）',
    data_freshness VARCHAR(32) COMMENT '数据新鲜度（FRESH/STALE）',
    business_value DECIMAL(10,2) COMMENT '业务价值评分（0-100）',
    technical_quality DECIMAL(10,2) COMMENT '技术质量评分（0-100）',
    security_level DECIMAL(10,2) COMMENT '安全等级评分（0-100）',
    overall_score DECIMAL(10,2) COMMENT '综合评分（0-100）',
    evaluator VARCHAR(128) COMMENT '评估人',
    evaluation_comment TEXT COMMENT '评估备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_asset_date (asset_id, evaluation_date),
    INDEX idx_asset_id (asset_id),
    INDEX idx_evaluation_date (evaluation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产评估指标表';
```

#### 3.1.5 资产生命周期表 (asset_lifecycle)
```sql
CREATE TABLE asset_lifecycle (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    lifecycle_event VARCHAR(64) NOT NULL COMMENT '生命周期事件（CREATE/UPDATE/DEPRECATE/ARCHIVE/DELETE）',
    event_description TEXT COMMENT '事件描述',
    operator VARCHAR(128) COMMENT '操作人',
    operated_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_asset_id (asset_id),
    INDEX idx_operated_time (operated_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产生命周期表';
```

## 4. 核心功能实现

### 4.1 资产目录管理

#### 4.1.1 资产信息管理
```java
@RestController
@RequestMapping("/api/assets")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    /**
     * 创建资产
     */
    @PostMapping
    public ResponseEntity<AssetInfoDTO> createAsset(@RequestBody CreateAssetDTO createDTO) {
        AssetInfoDTO asset = assetService.createAsset(createDTO);
        return ResponseEntity.ok(asset);
    }
    
    /**
     * 获取资产列表
     */
    @GetMapping
    public ResponseEntity<PageResult<AssetInfoDTO>> getAssets(
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String businessDomain,
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        AssetQueryDTO queryDTO = AssetQueryDTO.builder()
                .assetType(assetType)
                .businessDomain(businessDomain)
                .owner(owner)
                .search(search)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<AssetInfoDTO> pageResult = assetService.getAssets(queryDTO);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取资产详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetDetailDTO> getAssetDetail(@PathVariable Long id) {
        AssetDetailDTO assetDetail = assetService.getAssetDetail(id);
        return ResponseEntity.ok(assetDetail);
    }
    
    /**
     * 更新资产
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetInfoDTO> updateAsset(@PathVariable Long id, @RequestBody UpdateAssetDTO updateDTO) {
        AssetInfoDTO asset = assetService.updateAsset(id, updateDTO);
        return ResponseEntity.ok(asset);
    }
}
```

#### 4.1.2 资产服务实现
```java
@Service
public class AssetServiceImpl implements AssetService {
    
    @Autowired
    private AssetInfoMapper assetInfoMapper;
    
    @Autowired
    private AssetTagRelationMapper assetTagRelationMapper;
    
    @Autowired
    private AssetEvaluationMapper assetEvaluationMapper;
    
    @Autowired
    private AssetLifecycleMapper assetLifecycleMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    
    @Override
    @Transactional
    public AssetInfoDTO createAsset(CreateAssetDTO createDTO) {
        // 1. 校验资产编码唯一性
        AssetInfo existingAsset = assetInfoMapper.selectByAssetCode(createDTO.getAssetCode());
        if (existingAsset != null) {
            throw new BusinessException("资产编码已存在");
        }
        
        // 2. 创建资产信息
        AssetInfo assetInfo = new AssetInfo();
        BeanUtils.copyProperties(createDTO, assetInfo);
        assetInfo.setCreatedTime(new Date());
        assetInfo.setUpdatedTime(new Date());
        assetInfoMapper.insert(assetInfo);
        
        // 3. 创建生命周期记录
        AssetLifecycle lifecycle = new AssetLifecycle();
        lifecycle.setAssetId(assetInfo.getId());
        lifecycle.setLifecycleEvent("CREATE");
        lifecycle.setEventDescription("资产创建");
        lifecycle.setOperator(SecurityUtils.getCurrentUsername());
        lifecycle.setOperatedTime(new Date());
        assetLifecycleMapper.insert(lifecycle);
        
        // 4. 同步到ES
        syncToElasticsearch(assetInfo);
        
        // 5. 清除缓存
        redisTemplate.delete("asset:list");
        
        // 6. 返回结果
        AssetInfoDTO result = new AssetInfoDTO();
        BeanUtils.copyProperties(assetInfo, result);
        return result;
    }
    
    @Override
    public PageResult<AssetInfoDTO> getAssets(AssetQueryDTO queryDTO) {
        // 1. 尝试从缓存获取
        String cacheKey = "asset:list:" + JSON.toJSONString(queryDTO);
        PageResult<AssetInfoDTO> cachedResult = (PageResult<AssetInfoDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AssetInfo> assets = assetInfoMapper.selectByCondition(queryDTO);
        
        List<AssetInfoDTO> dtos = assets.stream()
                .map(asset -> {
                    AssetInfoDTO dto = new AssetInfoDTO();
                    BeanUtils.copyProperties(asset, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<AssetInfo> pageInfo = new PageInfo<>(assets);
        PageResult<AssetInfoDTO> result = PageResult.of(dtos, pageInfo.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
    
    @Override
    public AssetDetailDTO getAssetDetail(Long id) {
        // 1. 查询资产基本信息
        AssetInfo assetInfo = assetInfoMapper.selectById(id);
        if (assetInfo == null) {
            throw new BusinessException("资产不存在");
        }
        
        // 2. 查询资产标签
        List<AssetTag> tags = assetTagRelationMapper.selectTagsByAssetId(id);
        
        // 3. 查询最新评估信息
        AssetEvaluation latestEvaluation = assetEvaluationMapper.selectLatestByAssetId(id);
        
        // 4. 查询生命周期记录
        List<AssetLifecycle> lifecycles = assetLifecycleMapper.selectByAssetId(id);
        
        // 5. 构造详细信息DTO
        AssetDetailDTO detailDTO = new AssetDetailDTO();
        BeanUtils.copyProperties(assetInfo, detailDTO);
        detailDTO.setTags(tags.stream().map(tag -> {
            AssetTagDTO tagDTO = new AssetTagDTO();
            BeanUtils.copyProperties(tag, tagDTO);
            return tagDTO;
        }).collect(Collectors.toList()));
        
        if (latestEvaluation != null) {
            AssetEvaluationDTO evaluationDTO = new AssetEvaluationDTO();
            BeanUtils.copyProperties(latestEvaluation, evaluationDTO);
            detailDTO.setLatestEvaluation(evaluationDTO);
        }
        
        detailDTO.setLifecycles(lifecycles.stream().map(lifecycle -> {
            AssetLifecycleDTO lifecycleDTO = new AssetLifecycleDTO();
            BeanUtils.copyProperties(lifecycle, lifecycleDTO);
            return lifecycleDTO;
        }).collect(Collectors.toList()));
        
        return detailDTO;
    }
    
    @Override
    @Transactional
    public AssetInfoDTO updateAsset(Long id, UpdateAssetDTO updateDTO) {
        AssetInfo existingAsset = assetInfoMapper.selectById(id);
        if (existingAsset == null) {
            throw new BusinessException("资产不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingAsset);
        existingAsset.setUpdatedTime(new Date());
        assetInfoMapper.updateById(existingAsset);
        
        // 创建生命周期记录
        AssetLifecycle lifecycle = new AssetLifecycle();
        lifecycle.setAssetId(id);
        lifecycle.setLifecycleEvent("UPDATE");
        lifecycle.setEventDescription("资产更新");
        lifecycle.setOperator(SecurityUtils.getCurrentUsername());
        lifecycle.setOperatedTime(new Date());
        assetLifecycleMapper.insert(lifecycle);
        
        // 同步到ES
        syncToElasticsearch(existingAsset);
        
        // 清除缓存
        redisTemplate.delete("asset:list");
        redisTemplate.delete("asset:detail:" + id);
        
        AssetInfoDTO result = new AssetInfoDTO();
        BeanUtils.copyProperties(existingAsset, result);
        return result;
    }
    
    /**
     * 同步资产信息到Elasticsearch
     */
    private void syncToElasticsearch(AssetInfo assetInfo) {
        try {
            AssetDocument document = new AssetDocument();
            BeanUtils.copyProperties(assetInfo, document);
            elasticsearchTemplate.save(document);
        } catch (Exception e) {
            log.warn("同步资产到Elasticsearch失败: {}", e.getMessage());
        }
    }
}
```

### 4.2 资产分类与标签管理

#### 4.2.1 标签管理接口
```java
@RestController
@RequestMapping("/api/assets/tags")
public class AssetTagController {
    
    @Autowired
    private AssetTagService assetTagService;
    
    /**
     * 创建标签
     */
    @PostMapping
    public ResponseEntity<AssetTagDTO> createTag(@RequestBody CreateTagDTO createDTO) {
        AssetTagDTO tag = assetTagService.createTag(createDTO);
        return ResponseEntity.ok(tag);
    }
    
    /**
     * 获取标签列表
     */
    @GetMapping
    public ResponseEntity<List<AssetTagDTO>> getTags(@RequestParam(required = false) String tagCategory) {
        List<AssetTagDTO> tags = assetTagService.getTags(tagCategory);
        return ResponseEntity.ok(tags);
    }
    
    /**
     * 为资产打标签
     */
    @PostMapping("/{assetId}/tags")
    public ResponseEntity<Void> tagAsset(@PathVariable Long assetId, @RequestBody TagAssetDTO tagAssetDTO) {
        assetTagService.tagAsset(assetId, tagAssetDTO.getTagIds());
        return ResponseEntity.ok().build();
    }
    
    /**
     * 移除资产标签
     */
    @DeleteMapping("/{assetId}/tags/{tagId}")
    public ResponseEntity<Void> untagAsset(@PathVariable Long assetId, @PathVariable Long tagId) {
        assetTagService.untagAsset(assetId, tagId);
        return ResponseEntity.noContent().build();
    }
}
```

### 4.3 资产评估管理

#### 4.3.1 评估管理接口
```java
@RestController
@RequestMapping("/api/assets/evaluations")
public class AssetEvaluationController {
    
    @Autowired
    private AssetEvaluationService assetEvaluationService;
    
    /**
     * 创建资产评估
     */
    @PostMapping
    public ResponseEntity<AssetEvaluationDTO> createEvaluation(@RequestBody CreateEvaluationDTO createDTO) {
        AssetEvaluationDTO evaluation = assetEvaluationService.createEvaluation(createDTO);
        return ResponseEntity.ok(evaluation);
    }
    
    /**
     * 获取资产评估历史
     */
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetEvaluationDTO>> getEvaluations(@PathVariable Long assetId) {
        List<AssetEvaluationDTO> evaluations = assetEvaluationService.getEvaluations(assetId);
        return ResponseEntity.ok(evaluations);
    }
    
    /**
     * 获取资产评估趋势
     */
    @GetMapping("/trend/{assetId}")
    public ResponseEntity<List<EvaluationTrendDTO>> getEvaluationTrend(@PathVariable Long assetId) {
        List<EvaluationTrendDTO> trend = assetEvaluationService.getEvaluationTrend(assetId);
        return ResponseEntity.ok(trend);
    }
}
```

### 4.4 资产搜索功能

#### 4.4.1 搜索接口
```java
@RestController
@RequestMapping("/api/assets/search")
public class AssetSearchController {
    
    @Autowired
    private AssetSearchService assetSearchService;
    
    /**
     * 全文搜索资产
     */
    @GetMapping
    public ResponseEntity<PageResult<AssetInfoDTO>> searchAssets(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<AssetInfoDTO> pageResult = assetSearchService.searchAssets(keyword, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 高级搜索资产
     */
    @PostMapping("/advanced")
    public ResponseEntity<PageResult<AssetInfoDTO>> advancedSearch(@RequestBody AdvancedSearchDTO searchDTO) {
        PageResult<AssetInfoDTO> pageResult = assetSearchService.advancedSearch(searchDTO);
        return ResponseEntity.ok(pageResult);
    }
}
```

## 5. 接口设计

### 5.1 资产管理接口

#### 5.1.1 创建资产
- **接口地址**：POST /api/assets
- **请求体**：
```json
{
  "assetCode": "ASSET_TABLE_TOLL_TRANSACTION",
  "assetName": "toll_transaction_table",
  "assetNameCn": "收费交易表",
  "assetDescription": "收费交易明细数据表",
  "assetType": "TABLE",
  "businessDomain": "收费业务",
  "dataSource": "收费系统",
  "owner": "张三",
  "department": "收费中心",
  "schemaName": "main_dwd",
  "tableName": "dwd_toll_transaction_detail",
  "sensitivityLevel": "INTERNAL"
}
```

#### 5.1.2 获取资产列表
- **接口地址**：GET /api/assets
- **请求参数**：
  - assetType (String, optional)：资产类型
  - businessDomain (String, optional)：业务域
  - owner (String, optional)：负责人
  - search (String, optional)：搜索关键字
  - pageNum (Integer, optional, default=1)：页码
  - pageSize (Integer, optional, default=10)：每页大小
- **响应示例**：
```json
{
  "data": [
    {
      "id": 1,
      "assetCode": "ASSET_TABLE_TOLL_TRANSACTION",
      "assetName": "toll_transaction_table",
      "assetNameCn": "收费交易表",
      "assetDescription": "收费交易明细数据表",
      "assetType": "TABLE",
      "businessDomain": "收费业务",
      "dataSource": "收费系统",
      "owner": "张三",
      "department": "收费中心",
      "schemaName": "main_dwd",
      "tableName": "dwd_toll_transaction_detail",
      "sensitivityLevel": "INTERNAL",
      "assetStatus": "ACTIVE",
      "createdTime": "2025-12-09T10:30:00",
      "updatedTime": "2025-12-09T10:30:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

#### 5.1.3 获取资产详情
- **接口地址**：GET /api/assets/{id}
- **路径参数**：
  - id (Long)：资产ID
- **响应示例**：
```json
{
  "id": 1,
  "assetCode": "ASSET_TABLE_TOLL_TRANSACTION",
  "assetName": "toll_transaction_table",
  "assetNameCn": "收费交易表",
  "assetDescription": "收费交易明细数据表",
  "assetType": "TABLE",
  "businessDomain": "收费业务",
  "dataSource": "收费系统",
  "owner": "张三",
  "department": "收费中心",
  "schemaName": "main_dwd",
  "tableName": "dwd_toll_transaction_detail",
  "sensitivityLevel": "INTERNAL",
  "assetStatus": "ACTIVE",
  "tags": [
    {
      "id": 1,
      "tagCode": "TAG_BUSINESS_REVENUE",
      "tagName": "营收类",
      "tagDescription": "营收相关数据资产",
      "tagCategory": "业务标签",
      "tagColor": "#FF6B6B"
    }
  ],
  "latestEvaluation": {
    "id": 1,
    "evaluationDate": "2025-12-09",
    "assetLevel": "CORE",
    "usageFrequency": "HIGH",
    "dataFreshness": "FRESH",
    "businessValue": 95.5,
    "technicalQuality": 92.0,
    "securityLevel": 88.5,
    "overallScore": 92.0,
    "evaluator": "李四",
    "evaluationComment": "高质量核心资产"
  },
  "lifecycles": [
    {
      "id": 1,
      "lifecycleEvent": "CREATE",
      "eventDescription": "资产创建",
      "operator": "张三",
      "operatedTime": "2025-12-09T10:30:00"
    }
  ]
}
```

### 5.2 标签管理接口

#### 5.2.1 创建标签
- **接口地址**：POST /api/assets/tags
- **请求体**：
```json
{
  "tagCode": "TAG_BUSINESS_REVENUE",
  "tagName": "营收类",
  "tagDescription": "营收相关数据资产",
  "tagCategory": "业务标签",
  "tagColor": "#FF6B6B"
}
```

#### 5.2.2 为资产打标签
- **接口地址**：POST /api/assets/{assetId}/tags
- **路径参数**：
  - assetId (Long)：资产ID
- **请求体**：
```json
{
  "tagIds": [1, 2, 3]
}
```

### 5.3 评估管理接口

#### 5.3.1 创建资产评估
- **接口地址**：POST /api/assets/evaluations
- **请求体**：
```json
{
  "assetId": 1,
  "assetLevel": "CORE",
  "usageFrequency": "HIGH",
  "dataFreshness": "FRESH",
  "businessValue": 95.5,
  "technicalQuality": 92.0,
  "securityLevel": 88.5,
  "evaluator": "李四",
  "evaluationComment": "高质量核心资产"
}
```

### 5.4 搜索接口

#### 5.4.1 全文搜索资产
- **接口地址**：GET /api/assets/search
- **请求参数**：
  - keyword (String)：搜索关键字
  - pageNum (Integer, optional, default=1)：页码
  - pageSize (Integer, optional, default=10)：每页大小
- **响应示例**：
```json
{
  "data": [
    {
      "id": 1,
      "assetCode": "ASSET_TABLE_TOLL_TRANSACTION",
      "assetName": "toll_transaction_table",
      "assetNameCn": "收费交易表",
      "assetDescription": "收费交易明细数据表",
      "assetType": "TABLE",
      "businessDomain": "收费业务",
      "dataSource": "收费系统",
      "owner": "张三",
      "department": "收费中心"
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
1. **资产列表缓存**：将常用的资产查询结果缓存到Redis中
2. **资产详情缓存**：将资产详情信息缓存到Redis中
3. **标签缓存**：将标签信息缓存到Redis中

### 6.3 搜索优化
1. **Elasticsearch索引优化**：合理设计索引结构和分词器
2. **搜索结果缓存**：对热门搜索关键词的结果进行缓存
3. **异步同步**：资产信息变更时异步同步到Elasticsearch

## 7. 安全设计

### 7.1 认证授权
1. **JWT Token**：使用JWT进行用户身份认证
2. **RBAC权限控制**：基于角色的访问控制，不同角色具有不同的操作权限
3. **接口权限校验**：每个接口都需要进行权限校验

### 7.2 数据安全
1. **敏感数据脱敏**：对敏感资产信息进行脱敏处理
2. **操作日志记录**：记录所有资产变更操作日志
3. **数据备份**：定期对资产数据进行备份

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
│  │ Asset     │  │ │  │ Asset     │  │ │  │ Asset     │  │
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
3. **Elasticsearch索引创建**：创建资产搜索所需的索引
4. **配置文件调整**：根据实际环境修改application.yml配置
5. **服务打包**：使用Maven打包成jar文件
6. **服务部署**：将jar文件部署到服务器并启动
7. **负载均衡配置**：配置Nginx或Nacos进行负载均衡

## 9. 监控与运维

### 9.1 监控指标
1. **接口响应时间**：监控各接口的平均响应时间
2. **数据库连接数**：监控数据库连接池使用情况
3. **缓存命中率**：监控Redis缓存命中率
4. **搜索性能**：监控Elasticsearch搜索性能

### 9.2 日志管理
1. **操作日志**：记录所有用户操作日志
2. **错误日志**：记录系统错误和异常信息
3. **性能日志**：记录关键接口的性能数据
4. **审计日志**：记录资产变更的审计信息

### 9.3 告警机制
1. **接口超时告警**：接口响应时间超过阈值时告警
2. **数据库连接告警**：数据库连接数达到上限时告警
3. **缓存异常告警**：Redis连接异常或命中率过低时告警
4. **搜索性能告警**：Elasticsearch搜索响应时间过长时告警