-- 创建业务报表视图，整合多个维度的数据
CREATE VIEW business_intelligence_report AS
SELECT 
    ao.asset_code,
    ao.asset_name,
    ao.asset_name_cn,
    ao.overall_score,
    qar.check_date,
    qar.check_result,
    qar.error_rate,
    sjs.job_name,
    sjs.success_rate,
    rc.report_name,
    rc.report_type
FROM asset_overview ao
LEFT JOIN quality_analysis_report qar ON ao.asset_code = qar.table_name
LEFT JOIN scheduler_job_statistics sjs ON sjs.job_name LIKE CONCAT('%', ao.asset_code, '%')
LEFT JOIN report_config rc ON rc.report_code LIKE CONCAT('%', ao.asset_code, '%')
WHERE ao.evaluation_date >= DATE_SUB(CURDATE(), INTERVAL 90 DAY);