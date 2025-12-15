-- 创建资产生命周期分析视图，追踪资产从创建到废弃的全过程
CREATE VIEW asset_lifecycle_analysis AS
SELECT 
    ai.asset_code,
    ai.asset_name,
    ai.created_time AS asset_created_time,
    al.lifecycle_event AS lifecycle_stage,
    al.operated_time AS stage_start_time,
    NOW() AS stage_end_time,
    al.operator AS responsible_person,
    al.event_description AS comments,
    DATEDIFF(NOW(), al.operated_time) AS stage_duration_days,
    ae.overall_score,
    CASE 
        WHEN qcr.expected_value IS NOT NULL AND qcr.expected_value > 0 THEN 
            (qcr.actual_value / qcr.expected_value) 
        ELSE 0 
    END AS error_rate
FROM asset_info ai
JOIN asset_lifecycle al ON ai.id = al.asset_id
LEFT JOIN asset_evaluation ae ON ai.id = ae.asset_id 
    AND ae.evaluation_date = (
        SELECT MAX(evaluation_date) 
        FROM asset_evaluation ae2 
        WHERE ae2.asset_id = ai.id
    )
LEFT JOIN quality_check_result qcr ON ai.asset_code = qcr.table_name
    AND qcr.check_date = (
        SELECT MAX(check_date) 
        FROM quality_check_result qcr2 
        WHERE qcr2.table_name = ai.asset_code
    )
ORDER BY ai.asset_code, al.operated_time;