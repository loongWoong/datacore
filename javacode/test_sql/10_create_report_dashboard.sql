-- 创建报表仪表板视图，整合各类关键指标
CREATE VIEW report_dashboard AS
SELECT 
    'Asset Count' AS metric_name,
    COUNT(*) AS metric_value,
    'Total number of data assets' AS description,
    CURDATE() AS report_date
FROM asset_info

UNION ALL

SELECT 
    'Average Asset Score' AS metric_name,
    ROUND(AVG(overall_score), 2) AS metric_value,
    'Average score of all data assets' AS description,
    CURDATE() AS report_date
FROM asset_evaluation
WHERE evaluation_date = (
    SELECT MAX(evaluation_date) 
    FROM asset_evaluation
)

UNION ALL

SELECT 
    'Quality Check Pass Rate' AS metric_name,
    ROUND(SUM(CASE WHEN check_status = 'PASS' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS metric_value,
    'Overall data quality pass rate' AS description,
    CURDATE() AS report_date
FROM quality_check_result
WHERE check_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)

UNION ALL

SELECT 
    'Active Jobs' AS metric_name,
    COUNT(*) AS metric_value,
    'Number of active scheduler jobs' AS description,
    CURDATE() AS report_date
FROM xxl_job_info
WHERE trigger_status = 1

UNION ALL

SELECT 
    'Tables with High Lineage' AS metric_name,
    COUNT(*) AS metric_value,
    'Rules with high transformation complexity' AS description,
    CURDATE() AS report_date
FROM lineage_parse_rule
WHERE is_enabled = 1;