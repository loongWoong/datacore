-- 创建数据质量报告视图，关联质量检查结果和规则
CREATE VIEW quality_analysis_report AS
SELECT 
    qcr.table_name,
    qcr.column_name,
    qcr.check_date,
    qcr.check_status AS check_result,
    qcr.actual_value AS error_count,
    CASE 
        WHEN qcr.expected_value IS NOT NULL THEN qcr.expected_value
        ELSE 0 
    END AS total_count,
    CASE 
        WHEN qcr.expected_value IS NOT NULL AND qcr.expected_value > 0 THEN 
            (qcr.actual_value / qcr.expected_value) 
        ELSE 0 
    END AS error_rate,
    qr.rule_name,
    qr.rule_description,
    qr.rule_type,
    qr.severity_level
FROM quality_check_result qcr
JOIN quality_rule qr ON qcr.rule_id = qr.id
WHERE qcr.check_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY);