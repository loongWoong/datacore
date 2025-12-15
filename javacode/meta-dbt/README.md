# MySQL数据库元数据扫描与DBT模型生成工具

该工具包包含两个Python脚本，用于扫描MySQL数据库的元数据并自动生成对应的dbt模型文件。

## 文件说明

1. `scan_mysql_metadata.py` - 扫描MySQL数据库元数据
2. `generate_dbt_models.py` - 根据扫描结果生成dbt模型文件
3. `collect_and_upload.py` - 采集元数据和血缘信息并通过网关服务上传
4. `requirements.txt` - Python依赖包列表

## 安装依赖

首先安装所需的Python包：

```bash
pip install -r requirements.txt
```

## 使用步骤

### 第一步：扫描数据库元数据

运行以下命令扫描数据库元数据：

```bash
python scan_mysql_metadata.py
```

此脚本会：
1. 从 `.dbt/profiles.yml` 读取数据库连接配置
2. 连接到MySQL数据库
3. 扫描所有表的结构信息（列、索引等）
4. 将结果保存到 `metadata_scan_result.yaml` 文件中

### 第二步：生成dbt模型文件

运行以下命令生成dbt模型：

```bash
python generate_dbt_models.py
```

此脚本会：
1. 读取 `metadata_scan_result.yaml` 中的元数据
2. 为每个表生成对应的dbt模型SQL文件（存放在 `models/` 目录下）
3. 更新 `models/sources.yml` 文件，添加表和列的描述信息

### 第三步：采集并上传元数据和血缘信息

运行以下命令采集元数据和血缘信息并通过网关服务上传到元数据服务和血缘服务：

```bash
python collect_and_upload.py
```

此脚本会：
1. 从 `.dbt/profiles.yml` 读取数据库schema配置
2. 运行dbt项目并收集openlineage血缘信息
3. 从 `metadata_scan_result.yaml` 读取元数据
4. 通过网关服务将元数据上传到元数据服务
5. 通过网关服务将血缘信息上传到血缘服务

## 配置说明

数据库连接配置存储在 `.dbt/profiles.yml` 文件中，请确保其中的配置信息正确：

```yaml
myproject:
  target: dev
  outputs:
    dev:
      type: oceanbase_mysql      # 数据库类型
      host: 192.168.22.212       # 主机地址
      port: 3306                 # 端口号
      user: root                 # 用户名
      password: 123456           # 密码
      database: dataplatform     # 数据库名
      schema: dataplatform       # 模式名
      charset: utf8mb4           # 字符集
```

## 生成的文件

1. `metadata_scan_result.yaml` - 数据库元数据扫描结果
2. `models/{table_name}.sql` - 每个表对应的dbt模型文件
3. `models/sources.yml` - 更新后的数据源定义文件

## 注意事项

1. 确保网络可以访问配置文件中指定的数据库服务器
2. 确保提供的用户名和密码具有足够的权限来读取表结构信息
3. 生成的模型文件可能需要根据实际业务需求进行手动调整
4. 脚本假设MySQL数据库使用的是标准SQL语法
5. 运行 `collect_and_upload.py` 前确保网关服务、元数据服务和血缘服务都在运行
6. `collect_and_upload.py` 脚本会自动从 `.dbt/profiles.yml` 读取schema名称并正确上传

## 数据类型映射

脚本会自动将MySQL数据类型映射到dbt类型：

| MySQL类型 | DBT类型 |
|-----------|---------|
| INT, TINYINT, SMALLINT, MEDIUMINT, BIGINT | integer |
| DECIMAL, NUMERIC, FLOAT, DOUBLE | decimal |
| CHAR, VARCHAR, TEXT, TINYTEXT, MEDIUMTEXT, LONGTEXT | string |
| DATE | date |
| DATETIME, TIMESTAMP, TIME | timestamp |
| YEAR | integer |
| BOOL, BOOLEAN | boolean |
| BLOB, BINARY, VARBINARY | string |

## 自定义配置

如需修改生成的模型模板或其他行为，可以直接编辑 `generate_dbt_models.py` 文件中的相关函数。