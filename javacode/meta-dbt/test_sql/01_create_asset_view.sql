-- 创建资产总览视图，整合资产基本信息和评估信息
CREATE VIEW asset_overview AS
SELECT 
    ai.asset_code,
    ai.asset_name,
    ai.asset_name_cn,
    ai.asset_description,
    ae.evaluation_date,
    ae.asset_level,
    ae.usage_frequency,
    ae.data_freshness,
    ae.business_value,
    ae.technical_quality,
    ae.security_level,
    ae.overall_score
FROM asset_info ai
JOIN asset_evaluation ae ON ai.id = ae.asset_id
WHERE ae.evaluation_date = (
    SELECT MAX(evaluation_date) 
    FROM asset_evaluation ae2 
    WHERE ae2.asset_id = ai.id
);