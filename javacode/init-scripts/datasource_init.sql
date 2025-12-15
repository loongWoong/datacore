/*
 * 数据源管理服务数据库初始化脚本
 */

-- 创建数据源信息表
CREATE TABLE IF NOT EXISTS `datasource_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '数据源名称',
  `type` varchar(50) NOT NULL COMMENT '数据源类型',
  `host` varchar(100) NOT NULL COMMENT '主机地址',
  `port` int(11) NOT NULL COMMENT '端口号',
  `database_name` varchar(100) NOT NULL COMMENT '数据库名称',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `connection_url` varchar(500) NOT NULL COMMENT '连接URL',
  `driver_class` varchar(200) DEFAULT NULL COMMENT '驱动类',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `owner` varchar(100) DEFAULT NULL COMMENT '负责人',
  `environment` varchar(50) DEFAULT NULL COMMENT '环境',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_owner` (`owner`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源信息表';

-- 插入示例数据
INSERT INTO `datasource_info` (`name`, `type`, `host`, `port`, `database_name`, `username`, `password`, `connection_url`, `driver_class`, `description`, `owner`, `environment`) VALUES
('本地MySQL数据库', 'MYSQL', 'localhost', 3306, 'test_db', 'root', '123456', 'jdbc:mysql://localhost:3306/test_db', 'com.mysql.cj.jdbc.Driver', '本地测试数据库', '张三', 'DEV'),
('生产MySQL数据库', 'MYSQL', '192.168.1.100', 3306, 'prod_db', 'admin', 'password123', 'jdbc:mysql://192.168.1.100:3306/prod_db', 'com.mysql.cj.jdbc.Driver', '生产环境数据库', '李四', 'PROD'),
('PostgreSQL数据仓库', 'POSTGRESQL', '192.168.1.200', 5432, 'data_warehouse', 'dw_user', 'dw_password', 'jdbc:postgresql://192.168.1.200:5432/data_warehouse', 'org.postgresql.Driver', '数据仓库', '王五', 'PROD');