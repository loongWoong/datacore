-- 数据中台核心数据库初始化脚本
-- 数据库: datacore
-- 字符集: utf8mb4

-- 创建数据库
CREATE DATABASE IF NOT EXISTS datacore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE datacore;

-- 1. 用户表 (认证服务)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    full_name VARCHAR(100) COMMENT '全名',
    department VARCHAR(100) COMMENT '部门',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 2. 角色表 (认证服务)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_desc VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '角色表';

-- 3. 权限表 (认证服务)
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(100) NOT NULL UNIQUE COMMENT '权限名称',
    permission_desc VARCHAR(255) COMMENT '权限描述',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_path VARCHAR(255) COMMENT '资源路径',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '权限表';

-- 4. 用户角色关联表 (认证服务)
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 5. 角色权限关联表 (认证服务)
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联表';

-- 6. 审计日志表 (审计服务)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作类型',
    method VARCHAR(10) COMMENT '请求方法',
    url VARCHAR(255) COMMENT '请求URL',
    ip VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    execution_time BIGINT COMMENT '执行时间(ms)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-成功, 0-失败',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '审计日志表';

-- 7. 元数据表 (元数据服务)
CREATE TABLE IF NOT EXISTS metadata_entities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '元数据ID',
    entity_name VARCHAR(100) NOT NULL COMMENT '实体名称',
    entity_type VARCHAR(50) NOT NULL COMMENT '实体类型',
    database_name VARCHAR(100) COMMENT '数据库名',
    table_name VARCHAR(100) COMMENT '表名',
    description TEXT COMMENT '描述',
    owner VARCHAR(100) COMMENT '负责人',
    tags JSON COMMENT '标签',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '元数据实体表';

-- 8. 元数据属性表 (元数据服务)
CREATE TABLE IF NOT EXISTS metadata_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '属性ID',
    entity_id BIGINT NOT NULL COMMENT '实体ID',
    attribute_name VARCHAR(100) NOT NULL COMMENT '属性名称',
    attribute_type VARCHAR(50) NOT NULL COMMENT '属性类型',
    description TEXT COMMENT '描述',
    is_primary_key TINYINT DEFAULT 0 COMMENT '是否主键: 1-是, 0-否',
    is_nullable TINYINT DEFAULT 1 COMMENT '是否可空: 1-是, 0-否',
    position INT COMMENT '字段位置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '元数据属性表';

-- 9. 数据资产表 (资产服务)
CREATE TABLE IF NOT EXISTS data_assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资产ID',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    asset_type VARCHAR(50) NOT NULL COMMENT '资产类型',
    description TEXT COMMENT '描述',
    owner VARCHAR(100) COMMENT '负责人',
    department VARCHAR(100) COMMENT '所属部门',
    tags JSON COMMENT '标签',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '数据资产表';

-- 10. 资产评估表 (资产服务)
CREATE TABLE IF NOT EXISTS asset_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评估ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    evaluation_date DATE NOT NULL COMMENT '评估日期',
    score DECIMAL(5,2) COMMENT '评分',
    evaluator VARCHAR(100) COMMENT '评估人',
    comments TEXT COMMENT '评估备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '资产评估表';

-- 11. 资产标签表 (资产服务)
CREATE TABLE IF NOT EXISTS asset_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    tag_desc VARCHAR(255) COMMENT '标签描述',
    color VARCHAR(20) COMMENT '标签颜色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '资产标签表';

-- 12. 资产标签关联表 (资产服务)
CREATE TABLE IF NOT EXISTS asset_tag_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_asset_tag (asset_id, tag_id)
) COMMENT '资产标签关联表';

-- 13. 数据血缘表 (血缘服务)
CREATE TABLE IF NOT EXISTS data_lineage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '血缘ID',
    source_entity_id BIGINT COMMENT '源实体ID',
    target_entity_id BIGINT COMMENT '目标实体ID',
    relationship_type VARCHAR(50) COMMENT '关系类型',
    transformation_logic TEXT COMMENT '转换逻辑',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '数据血缘表';

-- 14. 数据质量规则表 (质量服务)
CREATE TABLE IF NOT EXISTS quality_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(50) NOT NULL COMMENT '规则类型',
    entity_id BIGINT COMMENT '实体ID',
    column_name VARCHAR(100) COMMENT '列名',
    rule_expression TEXT COMMENT '规则表达式',
    threshold_value VARCHAR(100) COMMENT '阈值',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '数据质量规则表';

-- 15. 数据质量检查结果表 (质量服务)
CREATE TABLE IF NOT EXISTS quality_check_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '检查结果ID',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    check_date DATE NOT NULL COMMENT '检查日期',
    check_result TINYINT COMMENT '检查结果: 1-通过, 0-不通过',
    actual_value VARCHAR(100) COMMENT '实际值',
    expected_value VARCHAR(100) COMMENT '期望值',
    deviation_rate DECIMAL(5,2) COMMENT '偏差率',
    details TEXT COMMENT '详情',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '数据质量检查结果表';

-- 16. 调度任务表 (调度服务)
CREATE TABLE IF NOT EXISTS schedule_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    job_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group VARCHAR(50) COMMENT '任务组',
    cron_expression VARCHAR(100) COMMENT 'Cron表达式',
    target_object VARCHAR(255) COMMENT '目标对象',
    target_method VARCHAR(100) COMMENT '目标方法',
    job_params TEXT COMMENT '任务参数',
    concurrent TINYINT DEFAULT 1 COMMENT '是否并发执行: 1-是, 0-否',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '调度任务表';

-- 17. 调度任务日志表 (调度服务)
CREATE TABLE IF NOT EXISTS schedule_job_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    job_id BIGINT NOT NULL COMMENT '任务ID',
    job_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group VARCHAR(50) COMMENT '任务组',
    execute_time TIMESTAMP COMMENT '执行时间',
    execute_result TINYINT COMMENT '执行结果: 1-成功, 0-失败',
    error_msg TEXT COMMENT '错误信息',
    execute_duration BIGINT COMMENT '执行时长(ms)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '调度任务日志表';

-- 18. 报表配置表 (报表服务)
CREATE TABLE IF NOT EXISTS report_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报表ID',
    report_name VARCHAR(100) NOT NULL COMMENT '报表名称',
    report_type VARCHAR(50) NOT NULL COMMENT '报表类型',
    data_source VARCHAR(100) COMMENT '数据源',
    query_sql TEXT COMMENT '查询SQL',
    chart_type VARCHAR(50) COMMENT '图表类型',
    config_params JSON COMMENT '配置参数',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '报表配置表';

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_metadata_entities_name ON metadata_entities(entity_name);
CREATE INDEX idx_data_assets_name ON data_assets(asset_name);
CREATE INDEX idx_quality_check_results_date ON quality_check_results(check_date);
CREATE INDEX idx_schedule_jobs_status ON schedule_jobs(status);
CREATE INDEX idx_report_configs_type ON report_configs(report_type);

-- 插入初始数据
-- 插入默认角色
INSERT INTO roles (role_name, role_desc) VALUES 
('ADMIN', '系统管理员'),
('USER', '普通用户'),
('AUDITOR', '审计员');

-- 插入默认权限
INSERT INTO permissions (permission_name, permission_desc, resource_type, resource_path) VALUES 
('USER_MANAGE', '用户管理', 'MENU', '/user'),
('ROLE_MANAGE', '角色管理', 'MENU', '/role'),
('PERMISSION_MANAGE', '权限管理', 'MENU', '/permission'),
('METADATA_MANAGE', '元数据管理', 'MENU', '/metadata'),
('ASSET_MANAGE', '资产管理', 'MENU', '/asset'),
('LINEAGE_VIEW', '血缘查看', 'MENU', '/lineage'),
('QUALITY_CHECK', '质量检查', 'MENU', '/quality'),
('SCHEDULE_MANAGE', '调度管理', 'MENU', '/schedule'),
('REPORT_VIEW', '报表查看', 'MENU', '/report'),
('AUDIT_LOG', '审计日志', 'MENU', '/audit');

-- 为管理员角色分配所有权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 1, id FROM permissions;

COMMIT;