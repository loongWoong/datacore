import yaml
import pymysql
import sys
import os

def load_db_config(profiles_path):
    """加载数据库配置"""
    with open(profiles_path, 'r', encoding='utf-8') as f:
        config = yaml.safe_load(f)
    
    db_config = config['myproject']['outputs']['dev']
    return {
        'host': db_config['host'],
        'port': db_config['port'],
        'user': db_config['user'],
        'password': db_config['password'],
        'database': db_config['database']
    }

class MySQLMetadataScanner:
    def __init__(self, host, port, user, password, database):
        self.host = host
        self.port = port
        self.user = user
        self.password = password
        self.database = database
        self.connection = None
    
    def connect(self):
        """连接到MySQL数据库"""
        self.connection = pymysql.connect(
            host=self.host,
            port=self.port,
            user=self.user,
            password=self.password,
            database=self.database,
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
    
    def disconnect(self):
        """断开数据库连接"""
        if self.connection:
            self.connection.close()
    
    def scan_database(self):
        """扫描数据库元数据"""
        if not self.connection:
            raise Exception("数据库未连接")
        
        metadata = {
            'database': self.database,
            'tables': []
        }
        
        with self.connection.cursor() as cursor:
            # 获取所有表名
            cursor.execute("SHOW TABLES")
            tables = cursor.fetchall()
            
            # 获取每个表的列信息
            for table_dict in tables:
                table_name = list(table_dict.values())[0]
                table_info = {
                    'name': table_name,
                    'columns': []
                }
                
                # 获取表的列信息
                cursor.execute(f"DESCRIBE `{table_name}`")
                columns = cursor.fetchall()
                
                for column in columns:
                    column_info = {
                        'name': column['Field'],
                        'type': column['Type'],
                        'nullable': column['Null'] == 'YES',
                        'key': column['Key'],
                        'default': column['Default'],
                        'extra': column['Extra']
                    }
                    table_info['columns'].append(column_info)
                
                metadata['tables'].append(table_info)
        
        return metadata

def save_metadata_to_yaml(metadata, output_path):
    """将元数据保存到YAML文件"""
    # 确保输出路径的目录存在
    output_dir = os.path.dirname(os.path.abspath(output_path))
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)
        
    with open(output_path, 'w', encoding='utf-8') as f:
        yaml.dump(metadata, f, allow_unicode=True, default_flow_style=False)

def main():
    """主函数"""
    # 获取输出文件名参数，默认为metadata_scan_result.yaml
    output_path = "metadata_scan_result.yaml"
    if len(sys.argv) > 1:
        output_path = sys.argv[1]
    
    # 优先使用环境变量中的DBT_PROFILES_DIR，否则使用默认路径
    dbt_profiles_dir = os.environ.get('DBT_PROFILES_DIR')
    if dbt_profiles_dir:
        profiles_path = os.path.join(dbt_profiles_dir, 'profiles.yml')
    else:
        profiles_path = '.dbt/profiles.yml'
    
    print(f"使用配置文件路径: {profiles_path}")
    print(f"输出文件路径: {output_path}")
    
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
        
        print(f"数据库元数据扫描完成! 输出文件: {output_path}")
        
    except Exception as e:
        print(f"扫描过程中出现错误: {e}")
        import traceback
        traceback.print_exc()
    finally:
        scanner.disconnect()


if __name__ == "__main__":
    main()