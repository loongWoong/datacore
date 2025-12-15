-- 数据中台完整数据库建库建表SQL脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS dataplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dataplatform;

-- ====================================================
-- 1. 元数据服务表结构 (metadata_service)
-- ====================================================

-- 1.1 元数据表信息表
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

-- 1.2 元数据字段信息表
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

-- 1.3 元数据版本信息表
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

-- ====================================================
-- 2. 数据资产服务表结构 (asset_service)
-- ====================================================

-- 2.1 数据资产信息表
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

-- 2.2 资产分类标签表
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

-- 2.3 资产标签关联表
CREATE TABLE asset_tag_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_asset_tag (asset_id, tag_id),
    INDEX idx_asset_id (asset_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产标签关联表';

-- 2.4 资产评估指标表
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

-- 2.5 资产生命周期表
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

-- ====================================================
-- 3. 数据质量服务表结构 (quality_service)
-- ====================================================

-- 3.1 质量规则表
CREATE TABLE quality_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    rule_code VARCHAR(128) NOT NULL COMMENT '规则编码',
    rule_name VARCHAR(256) NOT NULL COMMENT '规则名称',
    rule_description TEXT COMMENT '规则描述',
    rule_type VARCHAR(64) NOT NULL COMMENT '规则类型（完整性、准确性、一致性、唯一性等）',
    table_name VARCHAR(256) NOT NULL COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名（适用于字段级规则）',
    check_expression TEXT COMMENT '检查表达式（SQL片段）',
    expected_value VARCHAR(512) COMMENT '期望值',
    threshold DECIMAL(10,4) DEFAULT 0.0000 COMMENT '阈值',
    severity_level VARCHAR(32) DEFAULT 'MEDIUM' COMMENT '严重级别（LOW/MEDIUM/HIGH）',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_rule_code (rule_code),
    INDEX idx_table_name (table_name),
    INDEX idx_rule_type (rule_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据质量规则表';

-- 3.2 质量检查任务表
CREATE TABLE quality_check_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    job_name VARCHAR(256) NOT NULL COMMENT '任务名称',
    job_description TEXT COMMENT '任务描述',
    rule_ids TEXT COMMENT '关联的规则ID列表（逗号分隔）',
    cron_expression VARCHAR(128) COMMENT 'Cron表达式',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    last_execute_time DATETIME COMMENT '最后执行时间',
    next_execute_time DATETIME COMMENT '下次执行时间',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_next_execute_time (next_execute_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检查任务表';

-- 3.3 质量检查结果表
CREATE TABLE quality_check_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    job_id BIGINT NOT NULL COMMENT '任务ID',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    table_name VARCHAR(256) NOT NULL COMMENT '表名',
    column_name VARCHAR(128) COMMENT '字段名',
    check_date DATE NOT NULL COMMENT '检查日期',
    check_status VARCHAR(32) NOT NULL COMMENT '检查状态（PASS/FAIL/WARNING）',
    actual_value DECIMAL(20,4) COMMENT '实际值',
    expected_value DECIMAL(20,4) COMMENT '期望值',
    deviation DECIMAL(20,4) COMMENT '偏差值',
    failed_records LONGTEXT COMMENT '失败记录详情（JSON格式）',
    execution_time BIGINT COMMENT '执行耗时（毫秒）',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_job_id (job_id),
    INDEX idx_rule_id (rule_id),
    INDEX idx_table_name (table_name),
    INDEX idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检查结果表';

-- 3.4 质量报告表
CREATE TABLE quality_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_date DATE NOT NULL COMMENT '报告日期',
    table_name VARCHAR(256) NOT NULL COMMENT '表名',
    overall_score DECIMAL(10,2) COMMENT '综合质量得分',
    completeness_rate DECIMAL(10,2) COMMENT '完整性率',
    accuracy_rate DECIMAL(10,2) COMMENT '准确率',
    consistency_rate DECIMAL(10,2) COMMENT '一致率',
    uniqueness_rate DECIMAL(10,2) COMMENT '唯一性率',
    timeliness_rate DECIMAL(10,2) COMMENT '及时性率',
    total_records BIGINT COMMENT '总记录数',
    failed_rules INT COMMENT '失败规则数',
    warning_rules INT COMMENT '警告规则数',
    passed_rules INT COMMENT '通过规则数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_report_date_table (report_date, table_name),
    INDEX idx_report_date (report_date),
    INDEX idx_table_name (table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量报告表';

-- ====================================================
-- 4. 报表服务表结构 (report_service)
-- ====================================================

-- 4.1 报表配置表
CREATE TABLE report_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_code VARCHAR(128) NOT NULL COMMENT '报表编码',
    report_name VARCHAR(256) NOT NULL COMMENT '报表名称',
    report_description TEXT COMMENT '报表描述',
    report_type VARCHAR(64) NOT NULL COMMENT '报表类型（SIMPLE/TEMPLATE/DASHBOARD）',
    data_source_type VARCHAR(64) NOT NULL COMMENT '数据源类型（SQL/API）',
    data_source_config TEXT COMMENT '数据源配置（JSON格式）',
    template_file_path VARCHAR(512) COMMENT '模板文件路径',
    output_format VARCHAR(64) DEFAULT 'EXCEL' COMMENT '输出格式（EXCEL/PDF/HTML/CSV）',
    schedule_config TEXT COMMENT '调度配置（JSON格式）',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    creator VARCHAR(128) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_report_code (report_code),
    INDEX idx_report_type (report_type),
    INDEX idx_is_enabled (is_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表配置表';

-- 4.2 报表参数表
CREATE TABLE report_parameter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_id BIGINT NOT NULL COMMENT '报表ID',
    param_name VARCHAR(128) NOT NULL COMMENT '参数名称',
    param_label VARCHAR(256) NOT NULL COMMENT '参数标签',
    param_type VARCHAR(64) NOT NULL COMMENT '参数类型（STRING/INTEGER/DATE/BOOLEAN）',
    is_required TINYINT(1) DEFAULT 0 COMMENT '是否必填',
    default_value VARCHAR(512) COMMENT '默认值',
    param_options TEXT COMMENT '参数选项（JSON格式，用于下拉框等）',
    param_order INT DEFAULT 0 COMMENT '参数顺序',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_report_param (report_id, param_name),
    INDEX idx_report_id (report_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表参数表';

-- 4.3 报表生成记录表
CREATE TABLE report_generation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_id BIGINT NOT NULL COMMENT '报表ID',
    report_code VARCHAR(128) NOT NULL COMMENT '报表编码',
    report_name VARCHAR(256) NOT NULL COMMENT '报表名称',
    generation_status VARCHAR(32) NOT NULL COMMENT '生成状态（PENDING/RUNNING/SUCCESS/FAILED）',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    execution_time BIGINT COMMENT '执行耗时（毫秒）',
    file_path VARCHAR(512) COMMENT '生成文件路径',
    file_size BIGINT COMMENT '文件大小（字节）',
    error_message TEXT COMMENT '错误信息',
    generated_by VARCHAR(128) COMMENT '生成人',
    generated_params TEXT COMMENT '生成参数（JSON格式）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_report_id (report_id),
    INDEX idx_generation_status (generation_status),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表生成记录表';

-- 4.4 报表订阅表
CREATE TABLE report_subscription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    report_id BIGINT NOT NULL COMMENT '报表ID',
    subscriber VARCHAR(128) NOT NULL COMMENT '订阅人',
    subscription_type VARCHAR(64) NOT NULL COMMENT '订阅类型（EMAIL/WEBHOOK）',
    subscription_config TEXT NOT NULL COMMENT '订阅配置（JSON格式）',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否激活',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_report_subscriber (report_id, subscriber),
    INDEX idx_report_id (report_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表订阅表';

-- ====================================================
-- 5. 调度服务表结构 (scheduler_service) - XXL-JOB内置表
-- ====================================================

-- 5.1 任务信息表
CREATE TABLE xxl_job_info (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_group int(11) NOT NULL COMMENT '执行器主键ID',
    job_desc varchar(255) NOT NULL COMMENT '任务描述',
    add_time datetime DEFAULT NULL COMMENT '添加时间',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    author varchar(64) DEFAULT NULL COMMENT '作者',
    alarm_email varchar(255) DEFAULT NULL COMMENT '报警邮件',
    schedule_type varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
    schedule_conf varchar(128) DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    misfire_strategy varchar(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    executor_route_strategy varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
    executor_handler varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    executor_param text COMMENT '执行器任务参数',
    executor_block_strategy varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
    executor_timeout int(11) NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
    executor_fail_retry_count int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    glue_type varchar(50) NOT NULL COMMENT 'GLUE类型',
    glue_source mediumtext COMMENT 'GLUE源代码',
    glue_remark varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
    glue_updatetime datetime DEFAULT NULL COMMENT 'GLUE更新时间',
    child_jobid varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    trigger_status tinyint(4) NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
    trigger_last_time bigint(13) NOT NULL DEFAULT '0' COMMENT '上次调度时间',
    trigger_next_time bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务信息表';

-- 5.2 调度日志表
CREATE TABLE xxl_job_log (
    id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_group int(11) NOT NULL COMMENT '执行器主键ID',
    job_id int(11) NOT NULL COMMENT '任务，主键ID',
    executor_address varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    executor_handler varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
    executor_param text COMMENT '执行器任务参数',
    executor_sharding_param varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    executor_fail_retry_count int(11) NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    trigger_time datetime DEFAULT NULL COMMENT '调度-时间',
    trigger_code int(11) NOT NULL COMMENT '调度-结果',
    trigger_msg text COMMENT '调度-日志',
    handle_time datetime DEFAULT NULL COMMENT '执行-时间',
    handle_code int(11) NOT NULL COMMENT '执行-状态',
    handle_msg text COMMENT '执行-日志',
    alarm_status tinyint(4) NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认，1-无需告警，2-告警成功，3-告警失败',
    PRIMARY KEY (id),
    KEY I_trigger_time (trigger_time),
    KEY I_handle_code (handle_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调度日志表';

-- 5.3 执行器注册表
CREATE TABLE xxl_job_registry (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    registry_group varchar(50) NOT NULL COMMENT '注册组',
    registry_key varchar(255) NOT NULL COMMENT '注册键',
    registry_value varchar(255) NOT NULL COMMENT '注册值',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX I_g_k_v (registry_group, registry_key, registry_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行器注册表';

-- 5.4 任务日志报告表
CREATE TABLE xxl_job_log_report (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    trigger_day datetime DEFAULT NULL COMMENT '调度时间',
    running_count int(11) NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
    suc_count int(11) NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
    fail_count int(11) NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY I_trigger_day (trigger_day)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志报告表';

-- 5.5 任务GLUE日志表
CREATE TABLE xxl_job_logglue (
    id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    job_id int(11) NOT NULL COMMENT '任务，主键ID',
    glue_type varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
    glue_source mediumtext COMMENT 'GLUE源代码',
    glue_remark varchar(128) NOT NULL COMMENT 'GLUE备注',
    add_time datetime DEFAULT NULL COMMENT '添加时间',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务GLUE日志表';

-- ====================================================
-- 6. 数据溯源服务表结构 (lineage_service)
-- ====================================================

-- 6.1 血缘配置表
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

-- 6.2 血缘解析规则表
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