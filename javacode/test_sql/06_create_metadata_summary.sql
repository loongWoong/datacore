-- 创建元数据汇总视图，展示各层数据资产情况
CREATE VIEW metadata_layer_summary AS
SELECT 
    mt.data_layer,
    COUNT(DISTINCT mt.id) AS table_count,
    COUNT(DISTINCT mc.id) AS column_count,
    COUNT(DISTINCT ai.id) AS asset_count,
    AVG(ae.overall_score) AS avg_asset_score,
    COUNT(DISTINCT qcr.id) AS quality_check_count,
    AVG(
        CASE 
            WHEN qcr.expected_value IS NOT NULL AND qcr.expected_value > 0 THEN 
                (qcr.actual_value / qcr.expected_value) 
            ELSE 0 
        END
    ) AS avg_error_rate
FROM metadata_table_info mt
LEFT JOIN metadata_column_info mc ON mt.id = mc.table_id
LEFT JOIN asset_info ai ON mt.table_name = ai.asset_code
LEFT JOIN asset_evaluation ae ON ai.id = ae.asset_id
LEFT JOIN quality_check_result qcr ON mt.table_name = qcr.table_name
GROUP BY mt.data_layer
ORDER BY table_count DESC;