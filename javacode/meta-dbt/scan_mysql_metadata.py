#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MySQL数据库元数据扫描工具
用于扫描MySQL数据库表结构并生成dbt模型文件
"""

import pymysql
import yaml
import os
from typing import Dict, List, Any


class MySQLMetadataScanner:
    def __init__(self, host: str, port: int, user: str, password: str, database: str):
        """
        初始化MySQL元数据扫描器
        
        Args:
            host: 数据库主机地址
            port: 数据库端口
            user: 用户名
            password: 密码
            database: 数据库名
        """
        self.host = str(host)
        self.port = int(port)
        self.user = str(user)
        self.password = str(password)
        self.database = str(database)
        self.connection = None
    
    def connect(self):
        """建立数据库连接"""
        try:
            self.connection = pymysql.connect(
                host=str(self.host),
                port=int(self.port),
                user=str(self.user),
                password=str(self.password),
                database=str(self.database),
                charset='utf8mb4',
                cursorclass=pymysql.cursors.DictCursor
            )
            print(f"成功连接到数据库 {self.database}")
        except Exception as e:
            print(f"连接数据库失败: {e}")
            raise
    
    def disconnect(self):
        """关闭数据库连接"""
        if self.connection:
            self.connection.close()
            print("数据库连接已关闭")
    
    def get_tables(self) -> List[str]:
        """
        获取数据库中的所有表名
        
        Returns:
            表名列表
        """
        with self.connection.cursor() as cursor:
            cursor.execute("SHOW TABLES")
            tables = cursor.fetchall()
            
            # MySQL的SHOW TABLES结果中列名是Tables_in_{database}
            table_column = f"Tables_in_{self.database}"
            return [table[table_column] for table in tables]
    
    def get_table_columns(self, table_name: str) -> List[Dict[str, Any]]:
        """
        获取指定表的列信息
        
        Args:
            table_name: 表名
            
        Returns:
            列信息列表，每项包含列名、类型、是否为空、默认值等信息
        """
        with self.connection.cursor() as cursor:
            # 查询表结构
            cursor.execute(f"DESCRIBE `{table_name}`")
            columns = cursor.fetchall()
            return columns
    
    def get_table_indexes(self, table_name: str) -> List[Dict[str, Any]]:
        """
        获取指定表的索引信息
        
        Args:
            table_name: 表名
            
        Returns:
            索引信息列表
        """
        with self.connection.cursor() as cursor:
            cursor.execute(f"SHOW INDEX FROM `{table_name}`")
            indexes = cursor.fetchall()
            return indexes
    
    def scan_database(self) -> Dict[str, Any]:
        """
        扫描整个数据库的元数据
        
        Returns:
            包含所有表及其元数据的字典
        """
        metadata = {}
        tables = self.get_tables()
        
        print(f"发现 {len(tables)} 个表")
        
        for table in tables:
            print(f"正在扫描表: {table}")
            columns = self.get_table_columns(table)
            indexes = self.get_table_indexes(table)
            
            metadata[table] = {
                'columns': columns,
                'indexes': indexes
            }
        
        return metadata


def load_db_config(profiles_path: str, profile_name: str = 'myproject') -> Dict[str, Any]:
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
    
    # 处理dbt-mysql和oceanbase_mysql的差异
    db_type = config.get('type', 'mysql')
    if db_type == 'oceanbase_mysql':
        # OceanBase MySQL兼容模式
        host = str(config['host'])
        port = int(config['port'])
        user = str(config['user'])
        password = str(config['password'])
        database = str(config['database'])
    else:
        # 标准MySQL
        host = str(config['host'])
        port = int(config['port'])
        user = str(config['user'])
        password = str(config['password'])
        database = str(config['database'])
    
    return {
        'host': host,
        'port': port,
        'user': user,
        'password': password,
        'database': database
    }


def save_metadata_to_yaml(metadata: Dict[str, Any], output_path: str):
    """
    将元数据保存为YAML格式
    
    Args:
        metadata: 元数据字典
        output_path: 输出文件路径
    """
    with open(output_path, 'w', encoding='utf-8') as f:
        yaml.dump(metadata, f, allow_unicode=True, default_flow_style=False)
    
    print(f"元数据已保存到 {output_path}")


def main():
    """主函数"""
    # 配置文件路径
    profiles_path = '.dbt/profiles.yml'
    output_path = 'metadata_scan_result.yaml'
    
    # 加载数据库配置
    try:
        db_config = load_db_config(profiles_path)
        print("成功加载数据库配置")
    except Exception as e:
        print(f"加载数据库配置失败: {e}")
        return
    
    # 创建扫描器并连接数据库
    scanner = MySQLMetadataScanner(
        host=db_config['host'],
        port=db_config['port'],
        user=db_config['user'],
        password=db_config['password'],
        database=db_config['database']
    )
    
    try:
        scanner.connect()
        
        # 扫描数据库元数据
        print("开始扫描数据库元数据...")
        metadata = scanner.scan_database()
        
        # 保存结果
        save_metadata_to_yaml(metadata, output_path)
        
        print("数据库元数据扫描完成!")
        
    except Exception as e:
        print(f"扫描过程中出现错误: {e}")
    finally:
        scanner.disconnect()


if __name__ == "__main__":
    main()