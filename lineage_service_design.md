# 数据溯源服务详细设计方案

## 1. 概述

数据溯源服务是数据中台的重要组成部分，负责管理和展示数据在各个处理环节之间的血缘关系。该服务通过解析dbt模型和SQL语句，自动构建数据血缘图谱，并提供可视化的展示功能。服务基于Spring Boot框架构建，使用Neo4j图数据库存储血缘关系，提供高效的血缘查询和分析功能。

## 2. 技术架构

### 2.1 技术选型
- **框架**：Spring Boot 3.x + Spring Cloud Alibaba
- **图数据库**：Neo4j 5.x
- **SQL解析**：JSqlParser
- **接口文档**：SpringDoc Swagger UI
- **构建工具**：Maven

### 2.2 服务架构图
```
┌─────────────────────────────────────────────────────────────┐
│                   Lineage Service API Layer                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 血缘查询 │  │ 血缘构建 │  │ 图谱展示 │  │ 配置管理 │   │
│  │ Controller│  │ Controller│  │ Controller│  │ Controller│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 血缘服务 │  │ 构建服务 │  │ 图谱服务 │  │ 配置服务 │   │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ 血缘仓储 │  │ 构建篶储 │  │ 图谱仓储 │  │ 配置仓储 │   │
│  │ Repository│  │ Repository│  │ Repository│  │ Repository│   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   Storage Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │ Neo4j    │  │ MySQL    │  │ Redis    │                 │
│  │ (图数据库)│  │ (配置)    │  │ (缓存)    │                 │
│  └──────────┘  └──────────┘  └──────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

## 3. 数据库设计

### 3.1 Neo4j图数据库设计

#### 3.1.1 节点设计
1. **TableNode（表节点）**
   - tableName：表名
   - schemaName：模式名
   - fullName：完整表名（schema.table）
   - tableType：表类型（源表、中间表、结果表）
   - description：表描述
   - businessDomain：业务域
   - dataLayer：数据分层

2. **ColumnNode（字段节点）**
   - columnName：字段名
   - dataType：数据类型
   - description：字段描述
   - isPrimaryKey：是否主键

3. **JobNode（作业节点）**
   - jobName：作业名
   - jobType：作业类型（dbt模型、Spark作业等）
   - description：作业描述
   - schedule：调度信息

#### 3.1.2 关系设计
1. **DEPENDS_ON（依赖关系）**
   - sourceTable → targetTable
   - 表示源表被目标表依赖

2. **TRANSFORMED_FROM（转换来源）**
   - targetTable → sourceTable
   - 表示目标表是从源表转换而来

3. **CONTAINS（包含关系）**
   - table → column
   - 表示表包含字段

4. **EXECUTED_BY（执行关系）**
   - job → table
   - 表示作业生成了表

### 3.2 MySQL配置表设计

#### 3.2.1 血缘配置表 (lineage_config)
```sql
CREATE TABLE lineage_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    config_key VARCHAR(128) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(64) COMMENT '配置类型',
    description VARCHAR(256) COMMENT '配置描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血缘配置表';
```

#### 3.2.2 血缘解析规则表 (lineage_parse_rule)
```sql
CREATE TABLE lineage_parse_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(64) NOT NULL COMMENT '规则类型',
    source_pattern TEXT COMMENT '源模式',
    target_pattern TEXT COMMENT '目标模式',
    transform_type VARCHAR(64) COMMENT '转换类型',
    description TEXT COMMENT '规则描述',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='血缘解析规则表';
```

## 4. 核心功能实现

### 4.1 血缘关系构建

#### 4.1.1 dbt模型解析
```java
@Service
public class DbtLineageBuilder implements LineageBuilder {
    
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Override
    public void buildLineageFromDbt(String projectPath) {
        // 1. 解析dbt项目中的模型文件
        List<DbtModel> models = parseDbtModels(projectPath);
        
        // 2. 构建表节点
        for (DbtModel model : models) {
            TableNode tableNode = new TableNode();
            tableNode.setTableName(model.getName());
            tableNode.setSchemaName(model.getSchema());
            tableNode.setFullName(model.getSchema() + "." + model.getName());
            tableNode.setDescription(model.getDescription());
            tableNode.setDataLayer(model.getDataLayer());
            
            // 保存表节点
            neo4jTemplate.save(tableNode);
            
            // 3. 构建字段节点
            for (DbtColumn column : model.getColumns()) {
                ColumnNode columnNode = new ColumnNode();
                columnNode.setColumnName(column.getName());
                columnNode.setDataType(column.getDataType());
                columnNode.setDescription(column.getDescription());
                
                // 保存字段节点并与表节点建立关系
                neo4jTemplate.save(columnNode);
                neo4jTemplate.createRelationship(tableNode, columnNode, "CONTAINS");
            }
            
            // 4. 解析依赖关系
            List<String> refs = parseRefs(model.getSql());
            for (String ref : refs) {
                // 建立依赖关系
                TableNode sourceTable = neo4jTemplate.findOne(ref, TableNode.class);
                if (sourceTable != null) {
                    neo4jTemplate.createRelationship(sourceTable, tableNode, "DEPENDS_ON");
                    neo4jTemplate.createRelationship(tableNode, sourceTable, "TRANSFORMED_FROM");
                }
            }
        }
    }
    
    private List<String> parseRefs(String sql) {
        // 使用JSqlParser解析SQL中的ref函数调用
        List<String> refs = new ArrayList<>();
        // 解析逻辑...
        return refs;
    }
}
```

#### 4.1.2 SQL血缘解析
```java
@Service
public class SqlLineageParser {
    
    public LineageGraph parseSql(String sql, String tableName) {
        // 1. 使用JSqlParser解析SQL
        Statement statement = CCJSqlParserUtil.parse(sql);
        
        LineageGraph graph = new LineageGraph();
        
        if (statement instanceof Select) {
            Select select = (Select) statement;
            parseSelectStatement(select, graph, tableName);
        } else if (statement instanceof Insert) {
            Insert insert = (Insert) statement;
            parseInsertStatement(insert, graph, tableName);
        }
        
        return graph;
    }
    
    private void parseSelectStatement(Select select, LineageGraph graph, String targetTable) {
        // 解析SELECT语句的血缘关系
        SelectBody selectBody = select.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            
            // 解析FROM子句
            List<Table> fromTables = new ArrayList<>();
            FromItem fromItem = plainSelect.getFromItem();
            if (fromItem instanceof Table) {
                fromTables.add((Table) fromItem);
            }
            
            // 解析JOIN子句
            List<Join> joins = plainSelect.getJoins();
            if (joins != null) {
                for (Join join : joins) {
                    FromItem rightItem = join.getRightItem();
                    if (rightItem instanceof Table) {
                        fromTables.add((Table) rightItem);
                    }
                }
            }
            
            // 建立血缘关系
            for (Table fromTable : fromTables) {
                graph.addDependency(fromTable.getName(), targetTable);
            }
        }
    }
}
```

### 4.2 血缘关系查询

#### 4.2.1 血缘图谱查询
```java
@RestController
@RequestMapping("/api/lineage")
public class LineageController {
    
    @Autowired
    private LineageService lineageService;
    
    /**
     * 获取血缘关系图
     */
    @GetMapping("/graph")
    public ResponseEntity<LineageGraphDTO> getLineageGraph(
            @RequestParam(required = false) String tableName,
            @RequestParam(defaultValue = "3") Integer depth) {
        
        LineageGraph graph = lineageService.getLineageGraph(tableName, depth);
        
        LineageGraphDTO dto = new LineageGraphDTO();
        // 转换为DTO对象
        
        return ResponseEntity.ok(dto);
    }
    
    /**
     * 获取上游血缘关系
     */
    @GetMapping("/upstream/{tableName}")
    public ResponseEntity<List<LineageNodeDTO>> getUpstreamLineage(
            @PathVariable String tableName,
            @RequestParam(defaultValue = "3") Integer depth) {
        
        List<LineageNode> upstreamNodes = lineageService.getUpstreamLineage(tableName, depth);
        
        List<LineageNodeDTO> dtos = upstreamNodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * 获取下游血缘关系
     */
    @GetMapping("/downstream/{tableName}")
    public ResponseEntity<List<LineageNodeDTO>> getDownstreamLineage(
            @PathVariable String tableName,
            @RequestParam(defaultValue = "3") Integer depth) {
        
        List<LineageNode> downstreamNodes = lineageService.getDownstreamLineage(tableName, depth);
        
        List<LineageNodeDTO> dtos = downstreamNodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }
}
```

#### 4.2.2 血缘服务实现
```java
@Service
public class LineageServiceImpl implements LineageService {
    
    @Autowired
    private Neo4jTemplate neo4jTemplate;
    
    @Override
    public LineageGraph getLineageGraph(String tableName, Integer depth) {
        LineageGraph graph = new LineageGraph();
        
        // 1. 查询表节点
        TableNode centerTable = neo4jTemplate.findOne(
            "MATCH (t:TableNode {fullName: $fullName}) RETURN t", 
            Map.of("fullName", tableName), 
            TableNode.class
        );
        
        if (centerTable == null) {
            return graph;
        }
        
        graph.addNode(centerTable);
        
        // 2. 查询上游节点
        List<TableNode> upstreamNodes = neo4jTemplate.findAll(
            "MATCH (t:TableNode {fullName: $fullName})<-[:DEPENDS_ON*1..$depth]-(up:TableNode) RETURN DISTINCT up",
            Map.of("fullName", tableName, "depth", depth),
            TableNode.class
        );
        
        // 3. 查询下游节点
        List<TableNode> downstreamNodes = neo4jTemplate.findAll(
            "MATCH (t:TableNode {fullName: $fullName})-[:DEPENDS_ON*1..$depth]->(down:TableNode) RETURN DISTINCT down",
            Map.of("fullName", tableName, "depth", depth),
            TableNode.class
        );
        
        // 4. 添加节点到图谱
        upstreamNodes.forEach(graph::addNode);
        downstreamNodes.forEach(graph::addNode);
        
        // 5. 查询关系
        List<LineageRelationship> relationships = neo4jTemplate.findAll(
            "MATCH (a:TableNode)-[r:DEPENDS_ON]->(b:TableNode) WHERE a.fullName IN $nodeNames AND b.fullName IN $nodeNames RETURN r",
            Map.of("nodeNames", graph.getNodeNames()),
            LineageRelationship.class
        );
        
        relationships.forEach(graph::addRelationship);
        
        return graph;
    }
    
    @Override
    public List<LineageNode> getUpstreamLineage(String tableName, Integer depth) {
        return neo4jTemplate.findAll(
            "MATCH (t:TableNode {fullName: $fullName})<-[:DEPENDS_ON*1..$depth]-(up:TableNode) RETURN DISTINCT up",
            Map.of("fullName", tableName, "depth", depth),
            TableNode.class
        );
    }
    
    @Override
    public List<LineageNode> getDownstreamLineage(String tableName, Integer depth) {
        return neo4jTemplate.findAll(
            "MATCH (t:TableNode {fullName: $fullName})-[:DEPENDS_ON*1..$depth]->(down:TableNode) RETURN DISTINCT down",
            Map.of("fullName", tableName, "depth", depth),
            TableNode.class
        );
    }
}
```

## 5. 接口设计

### 5.1 血缘图谱接口

#### 5.1.1 获取血缘关系图
- **接口地址**：GET /api/lineage/graph
- **请求参数**：
  - tableName (String, optional)：中心表名
  - depth (Integer, optional, default=3)：血缘深度
- **响应示例**：
```json
{
  "nodes": [
    {
      "id": "main_dwd.dwd_toll_transaction_detail",
      "label": "dwd_toll_transaction_detail",
      "type": "table",
      "properties": {
        "schemaName": "main_dwd",
        "tableName": "dwd_toll_transaction_detail",
        "description": "收费交易明细表",
        "dataLayer": "DWD"
      }
    }
  ],
  "edges": [
    {
      "source": "main_staging.stg_toll_transaction",
      "target": "main_dwd.dwd_toll_transaction_detail",
      "type": "DEPENDS_ON",
      "properties": {
        "transformType": "清洗"
      }
    }
  ]
}
```

#### 5.1.2 获取上游血缘关系
- **接口地址**：GET /api/lineage/upstream/{tableName}
- **路径参数**：
  - tableName (String)：表名
- **请求参数**：
  - depth (Integer, optional, default=3)：血缘深度
- **响应示例**：
```json
[
  {
    "fullName": "main_staging.stg_toll_transaction",
    "tableName": "stg_toll_transaction",
    "schemaName": "main_staging",
    "description": "收费交易原始数据",
    "dataLayer": "STAGING"
  }
]
```

#### 5.1.3 获取下游血缘关系
- **接口地址**：GET /api/lineage/downstream/{tableName}
- **路径参数**：
  - tableName (String)：表名
- **请求参数**：
  - depth (Integer, optional, default=3)：血缘深度
- **响应示例**：
```json
[
  {
    "fullName": "main_dws.dws_toll_revenue_daily",
    "tableName": "dws_toll_revenue_daily",
    "schemaName": "main_dws",
    "description": "日收费收入汇总表",
    "dataLayer": "DWS"
  }
]
```

### 5.2 血缘构建接口

#### 5.2.1 触发dbt血缘构建
- **接口地址**：POST /api/lineage/build/dbt
- **请求体**：
```json
{
  "projectPath": "/path/to/dbt/project",
  "forceRebuild": false
}
```
- **响应示例**：
```json
{
  "success": true,
  "message": "血缘关系构建成功",
  "processedTables": 50,
  "processingTime": "2025-12-09T10:30:00"
}
```

#### 5.2.2 手动添加血缘关系
- **接口地址**：POST /api/lineage/relationship
- **请求体**：
```json
{
  "sourceTable": "main_staging.stg_toll_transaction",
  "targetTable": "main_dwd.dwd_toll_transaction_detail",
  "transformType": "清洗",
  "description": "数据清洗转换"
}
```

## 6. 性能优化措施

### 6.1 图数据库优化
1. **索引优化**：在表名、模式名等常用查询字段上建立索引
2. **关系优化**：合理设计关系类型，避免过多的关系类型
3. **缓存策略**：对热门血缘图谱进行缓存

### 6.2 查询优化
1. **深度限制**：限制血缘查询的最大深度，避免性能问题
2. **分页查询**：对大量节点的查询采用分页处理
3. **预计算**：对常用的血缘关系进行预计算和存储

### 6.3 构建优化
1. **增量构建**：支持增量构建血缘关系，只处理变化的部分
2. **并行处理**：对多个模型的血缘解析采用并行处理
3. **异步构建**：血缘构建任务采用异步方式进行

## 7. 安全设计

### 7.1 认证授权
1. **JWT Token**：使用JWT进行用户身份认证
2. **RBAC权限控制**：不同角色对血缘数据有不同的访问权限
3. **接口权限校验**：每个接口都需要进行权限校验

### 7.2 数据安全
1. **敏感表处理**：对敏感表的血缘关系进行特殊处理
2. **操作日志记录**：记录所有血缘关系变更操作日志
3. **数据备份**：定期对血缘数据进行备份

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
│  │ Lineage   │  │ │  │ Lineage   │  │ │  │ Lineage   │  │
│  │ Service   │  │ │  │ Service   │  │ │  │ Service   │  │
│  └───────────┘  │ │  └───────────┘  │ │  └───────────┘  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                    Shared Storage                           │
│  ┌───────────┐  ┌───────────┐  ┌─────────────────────────┐ │
│  │   Neo4j   │  │   MySQL   │  │   Redis                 │ │
│  │ (图数据库) │  │ (配置)     │  │ (缓存)                   │ │
│  └───────────┘  └───────────┘  └─────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 8.2 部署步骤
1. **环境准备**：安装JDK 17、Neo4j、MySQL、Redis
2. **数据库初始化**：执行数据库脚本创建表结构
3. **Neo4j配置**：配置Neo4j图数据库，建立索引
4. **配置文件调整**：根据实际环境修改application.yml配置
5. **服务打包**：使用Maven打包成jar文件
6. **服务部署**：将jar文件部署到服务器并启动
7. **负载均衡配置**：配置Nginx或Nacos进行负载均衡

## 9. 监控与运维

### 9.1 监控指标
1. **接口响应时间**：监控各接口的平均响应时间
2. **图数据库性能**：监控Neo4j的查询性能和内存使用
3. **血缘构建进度**：监控血缘关系构建任务的进度
4. **缓存命中率**：监控Redis缓存命中率

### 9.2 日志管理
1. **操作日志**：记录所有用户操作日志
2. **错误日志**：记录系统错误和异常信息
3. **性能日志**：记录关键接口的性能数据
4. **审计日志**：记录血缘关系变更的审计信息

### 9.3 告警机制
1. **接口超时告警**：接口响应时间超过阈值时告警
2. **数据库连接告警**：数据库连接数达到上限时告警
3. **构建失败告警**：血缘构建任务失败时告警
4. **系统资源告警**：CPU、内存使用率过高时告警