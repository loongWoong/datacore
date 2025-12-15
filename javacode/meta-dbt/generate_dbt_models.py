#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DBT模型生成器
将扫描到的MySQL元数据转换为dbt模型文件
"""

import yaml
import os
from typing import Dict, Any, List


def map_mysql_type_to_dbt(mysql_type: str) -> str:
    """
    将MySQL数据类型映射到dbt类型
    
    Args:
        mysql_type: MySQL数据类型字符串
        
    Returns:
        对应的dbt类型
    """
    # 转换为小写以便比较
    mysql_type = mysql_type.lower()
    
    # 数值类型
    if 'int' in mysql_type:
        return 'integer'
    elif 'decimal' in mysql_type or 'numeric' in mysql_type or 'float' in mysql_type or 'double' in mysql_type:
        return 'decimal'
    
    # 字符串类型
    elif 'char' in mysql_type or 'text' in mysql_type:
        return 'string'
    
    # 时间类型
    elif 'date' in mysql_type and 'time' not in mysql_type:
        return 'date'
    elif 'time' in mysql_type:
        return 'timestamp'
    elif 'year' in mysql_type:
        return 'integer'
    
    # 布尔类型
    elif 'bool' in mysql_type:
        return 'boolean'
    
    # 二进制类型
    elif 'blob' in mysql_type or 'binary' in mysql_type:
        return 'string'
    
    # 默认返回string
    else:
        return 'string'


def generate_model_sql(table_name: str, columns: List[Dict[str, Any]]) -> str:
    """
    生成dbt模型SQL文件内容
    
    Args:
        table_name: 表名
        columns: 列信息列表
        
    Returns:
        SQL文件内容
    """
    # 构建SELECT语句的字段列表
    selected_fields = []
    for col in columns:
        col_name = col['Field']
        dbt_type = map_mysql_type_to_dbt(col['Type'])
        
        # 添加字段别名和注释（如果需要）
        field_line = f"    {col_name}::{{{{ dbt.type_{dbt_type}() }}}} AS {col_name}"
        selected_fields.append(field_line)
    
    # 组装SQL内容
    sql_content = ('{% snapshot ' + table_name + '_snapshot %}\n'
                   '\n'
                   '{{\n'
                   '    config(\n'
                   "      target_database='dataplatform',\n"
                   "      target_schema='snapshots',\n"
                   "      unique_key='id',\n"
                   "      strategy='timestamp',\n"
                   "      updated_at='updated_at',\n"
                   '    )\n'
                   '}}\n'
                   '\n'
                   'SELECT\n'
                   + ',\n'.join(selected_fields) + '\n'
                   'FROM {{ source(\'dataplatform\', \'' + table_name + '\') }}\n'
                   '\n'
                   '{% endsnapshot %}')
    
    return sql_content


def generate_source_yml(source_name: str, database: str, schema: str, tables_metadata: Dict[str, Any]) -> str:
    """
    生成或更新source.yml文件内容
    
    Args:
        source_name: 数据源名称
        database: 数据库名
        schema: 模式名
        tables_metadata: 表元数据
        
    Returns:
        YAML文件内容
    """
    # 构建tables部分
    tables_list = []
    for table_name, metadata in tables_metadata.items():
        # 获取主键字段
        primary_keys = []
        for col in metadata['columns']:
            if col['Key'] == 'PRI':
                primary_keys.append(col['Field'])
        
        table_entry = {
            'name': table_name,
            'description': f"{table_name}表",
            'columns': []
        }
        
        # 添加列描述
        for col in metadata['columns']:
            col_desc = {
                'name': col['Field'],
                'description': f"{col['Field']}字段"
            }
            
            # 如果是主键，添加测试
            if col['Key'] == 'PRI':
                col_desc['tests'] = ['unique', 'not_null']
                
            table_entry['columns'].append(col_desc)
        
        tables_list.append(table_entry)
    
    # 构建完整的YAML结构
    source_data = {
        'version': 2,
        'sources': [
            {
                'name': source_name,
                'database': database,
                'schema': schema,
                'tables': tables_list
            }
        ]
    }
    
    return yaml.dump(source_data, allow_unicode=True, default_flow_style=False, indent=2)


def load_metadata_from_yaml(file_path: str) -> Dict[str, Any]:
    """
    从YAML文件加载元数据
    
    Args:
        file_path: YAML文件路径
        
    Returns:
        元数据字典
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


def save_model_sql(model_name: str, sql_content: str, output_dir: str = 'models'):
    """
    保存模型SQL文件
    
    Args:
        model_name: 模型名称
        sql_content: SQL内容
        output_dir: 输出目录
    """
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)
    
    # 保存文件
    file_path = os.path.join(output_dir, f"{model_name}.sql")
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(sql_content)
    
    print(f"模型文件已保存: {file_path}")


def main():
    """主函数"""
    # 输入文件和输出目录
    metadata_file = 'metadata_scan_result.yaml'
    models_dir = 'models'
    sources_file = os.path.join(models_dir, 'sources.yml')
    
    # 加载元数据
    try:
        metadata = load_metadata_from_yaml(metadata_file)
        print("成功加载元数据")
    except Exception as e:
        print(f"加载元数据失败: {e}")
        return
    
    # 为每个表生成模型文件
    for table_name, table_metadata in metadata.items():
        print(f"正在生成模型: {table_name}")
        
        # 生成SQL内容
        sql_content = generate_model_sql(table_name, table_metadata['columns'])
        
        # 保存SQL文件
        save_model_sql(table_name, sql_content, models_dir)
    
    # 更新或生成sources.yml
    print("正在更新sources.yml文件...")
    source_content = generate_source_yml(
        source_name='dataplatform',
        database='dataplatform',
        schema='dataplatform',
        tables_metadata=metadata
    )
    
    with open(sources_file, 'w', encoding='utf-8') as f:
        f.write(source_content)
    
    print(f"Sources文件已更新: {sources_file}")
    print("DBT模型生成完成!")


if __name__ == "__main__":
    main()