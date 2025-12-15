-- 创建质量趋势分析视图，展示数据质量随时间的变化
CREATE VIEW quality_trend_analysis AS
SELECT 
    table_name,
    column_name,
    DATE_FORMAT(check_date, '%Y-%m') AS check_month,
    COUNT(*) AS total_checks,
    SUM(CASE WHEN check_status = 'PASS' THEN 1 ELSE 0 END) AS pass_count,
    SUM(CASE WHEN check_status = 'FAIL' THEN 1 ELSE 0 END) AS fail_count,
    ROUND(SUM(CASE WHEN check_status = 'PASS' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS pass_rate,
    AVG(
        CASE 
            WHEN expected_value IS NOT NULL AND expected_value > 0 THEN 
                (actual_value / expected_value) 
            ELSE 0 
        END
    ) AS avg_error_rate,
    MIN(
        CASE 
            WHEN expected_value IS NOT NULL AND expected_value > 0 THEN 
                (actual_value / expected_value) 
            ELSE 0 
        END
    ) AS min_error_rate,
    MAX(
        CASE 
            WHEN expected_value IS NOT NULL AND expected_value > 0 THEN 
                (actual_value / expected_value) 
            ELSE 0 
        END
    ) AS max_error_rate
FROM quality_check_result
WHERE check_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH)
GROUP BY table_name, column_name, DATE_FORMAT(check_date, '%Y-%m')
ORDER BY table_name, column_name, check_month;