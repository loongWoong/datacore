#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
DBT元数据和血缘采集并上传工具
使用dbt和openlineage采集元数据和血缘信息，并通过网关服务上传到元数据服务和血缘服务
"""

import yaml
import json
import requests
import os
from typing import Dict, Any, List
import subprocess
import time

# 网关服务地址
GATEWAY_URL = "http://localhost:9000"

# 元数据服务API端点
METADATA_TABLES_API = f"{GATEWAY_URL}/api/metadata/tables"
METADATA_COLUMNS_API = f"{GATEWAY_URL}/api/metadata/columns"

# 血缘服务API端点
LINEAGE_BUILD_API = f"{GATEWAY_URL}/api/lineage/build"

def load_db_config(profiles_path: str = '.dbt/profiles.yml', profile_name: str = 'myproject') -> Dict[str, Any]:
    """
    从dbt profiles.yml加载数据库配置
    
    Args:
        profiles_path: profiles.yml文件路径
        profile_name: 配置名称
        
    Returns:
        数据库配置字典
    """
    with open(profiles_path, 'r', encoding='utf-8') as f:
        profiles = yaml.safe_load(f)
    
    # 获取dev环境配置
    config = profiles[profile_name]['outputs']['dev']
    
    return {
        'host': str(config['host']),
        'port': int(config['port']),
        'user': str(config['user']),
        'password': str(config['password']),
        'database': str(config['database']),
        'schema': str(config.get('schema', config['database']))  # schema可能与database相同
    }

class MetadataUploader:
    def __init__(self):
        self.session = requests.Session()
        self.session.headers.update({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
        # 从配置文件加载数据库schema信息
        try:
            self.db_config = load_db_config()
            self.schema_name = self.db_config['schema']
        except Exception as e:
            print(f"加载数据库配置失败，使用默认schema: dataplatform, 错误: {e}")
            self.schema_name = "dataplatform"
    
    def upload_table_metadata(self, table_name: str, table_info: Dict[str, Any]) -> bool:
        """
        上传表元数据到元数据服务
        
        Args:
            table_name: 表名
            table_info: 表信息
            
        Returns:
            上传是否成功
        """
        try:
            # 构造表元数据
            table_data = {
                "schemaName": self.schema_name,  # 从配置文件读取的schema名称
                "tableName": table_name,
                "tableNameCn": f"{table_name}表",
                "tableDescription": f"表 {table_name} 的元数据",
                "layer": "ods",  # 默认数据层
                "businessDomain": "default",  # 默认业务域
                "dataSource": f"mysql://{self.db_config['host']}:{self.db_config['port']}/{self.db_config['database']}",
                "updateFrequency": "daily",  # 默认更新频率
                "owner": "admin",  # 默认负责人
                "dataLayer": "ods"  # 默认数据分层
            }
            
            # 发送POST请求创建表元数据
            response = self.session.post(METADATA_TABLES_API, json=table_data)
            if response.status_code == 200:
                result = response.json()
                table_id = result.get('id', 0) if isinstance(result, dict) else 0
                print(f"成功上传表元数据: {table_name}, 表ID: {table_id}")
                return True
            else:
                print(f"上传表元数据失败: {table_name}, 状态码: {response.status_code}")
                print(f"响应内容: {response.text}")
                return False
                
        except Exception as e:
            print(f"上传表元数据异常: {table_name}, 错误: {e}")
            return False
    
    def upload_column_metadata(self, table_id: int, columns: List[Dict[str, Any]]) -> bool:
        """
        上传字段元数据到元数据服务
        
        Args:
            table_id: 表ID
            columns: 字段列表
            
        Returns:
            上传是否成功
        """
        try:
            success_count = 0
            for col in columns:
                # 构造字段元数据
                column_data = {
                    "tableId": table_id,
                    "columnName": col['Field'],
                    "columnType": col['Type'],
                    "isNullable": col['Null'] == 'YES',
                    "columnComment": f"字段 {col['Field']} 的描述",
                    "ordinalPosition": 0,  # 位置信息
                    "defaultValue": col.get('Default', ''),
                    "isPrimaryKey": col['Key'] == 'PRI'
                }
                
                # 发送POST请求创建字段元数据
                response = self.session.post(METADATA_COLUMNS_API, json=column_data)
                if response.status_code == 200:
                    print(f"成功上传字段元数据: {col['Field']}")
                    success_count += 1
                else:
                    print(f"上传字段元数据失败: {col['Field']}, 状态码: {response.status_code}")
                    
            return success_count == len(columns)
            
        except Exception as e:
            print(f"上传字段元数据异常: {e}")
            return False
    
    def upload_lineage_data(self, project_path: str) -> bool:
        """
        上传血缘数据到血缘服务
        
        Args:
            project_path: dbt项目路径
            
        Returns:
            上传是否成功
        """
        try:
            # 构造血缘构建请求
            lineage_data = {
                "projectPath": project_path
            }
            
            # 发送POST请求触发血缘构建
            response = self.session.post(f"{LINEAGE_BUILD_API}/dbt", json=lineage_data)
            if response.status_code == 200:
                print("成功触发血缘关系构建")
                return True
            else:
                print(f"触发血缘关系构建失败, 状态码: {response.status_code}")
                print(f"响应内容: {response.text}")
                return False
                
        except Exception as e:
            print(f"上传血缘数据异常: {e}")
            return False

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

def run_dbt_with_openlineage():
    """
    运行dbt并收集openlineage事件
    """
    try:
        # 设置openlineage环境变量
        env = os.environ.copy()
        env['OPENLINEAGE_URL'] = 'http://localhost:5000'  # OpenLineage收集器地址
        env['OPENLINEAGE_NAMESPACE'] = 'dbt-dataplatform'
        
        # 运行dbt命令
        print("正在运行dbt并收集血缘信息...")
        result = subprocess.run([
            'dbt', 'run',
            '--project-dir', '.',
            '--profiles-dir', '.dbt'
        ], cwd='.', env=env, capture_output=True, text=True)
        
        if result.returncode == 0:
            print("dbt运行成功")
            return True
        else:
            print(f"dbt运行失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"运行dbt异常: {e}")
        return False

def main():
    """主函数"""
    print("开始采集并上传元数据和血缘信息...")
    
    # 1. 运行dbt并收集血缘信息
    # 注意：这需要环境中已安装并配置好openlineage-collector
    # print("步骤1: 运行dbt并收集血缘信息")
    # if not run_dbt_with_openlineage():
    #     print("警告: dbt运行失败，将继续执行元数据上传")
    
    # 2. 上传元数据
    print("步骤2: 上传元数据到元数据服务")
    uploader = MetadataUploader()
    
    # 显示使用的schema名称
    print(f"使用数据库schema: {uploader.schema_name}")
    
    # 加载元数据
    metadata_file = 'metadata_scan_result.yaml'
    try:
        metadata = load_metadata_from_yaml(metadata_file)
        print("成功加载元数据")
    except Exception as e:
        print(f"加载元数据失败: {e}")
        return
    
    # 为每个表上传元数据
    for table_name, table_metadata in metadata.items():
        print(f"正在处理表: {table_name}")
        
        # 上传表元数据
        if uploader.upload_table_metadata(table_name, table_metadata):
            print(f"表 {table_name} 元数据上传成功")
        else:
            print(f"表 {table_name} 元数据上传失败")
    
    # 3. 上传血缘数据
    print("步骤3: 上传血缘数据到血缘服务")
    uploader.upload_lineage_data('.')
    
    print("元数据和血缘信息采集上传完成!")

if __name__ == "__main__":
    main()