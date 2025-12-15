-- 创建调度任务统计视图，分析任务执行情况
CREATE VIEW scheduler_job_statistics AS
SELECT 
    xji.job_desc AS job_name,
    xji.job_desc,
    xji.author,
    xji.schedule_type,
    xji.schedule_conf,
    COUNT(xjl.id) AS execution_count,
    AVG(TIMESTAMPDIFF(SECOND, xjl.trigger_time, xjl.handle_time)) AS avg_wait_time,
    AVG(xjl.executor_timeout) AS avg_execution_time,
    SUM(CASE WHEN xjl.handle_code = 200 THEN 1 ELSE 0 END) AS success_count,
    SUM(CASE WHEN xjl.handle_code != 200 THEN 1 ELSE 0 END) AS fail_count,
    ROUND(SUM(CASE WHEN xjl.handle_code = 200 THEN 1 ELSE 0 END) * 100.0 / COUNT(xjl.id), 2) AS success_rate
FROM xxl_job_info xji
LEFT JOIN xxl_job_log xjl ON xji.id = xjl.job_id
WHERE xjl.trigger_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY xji.id, xji.job_desc, xji.author, xji.schedule_type, xji.schedule_conf;