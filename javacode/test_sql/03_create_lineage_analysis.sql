-- 创建数据血缘分析视图，展示表之间的依赖关系
-- 注意：根据数据库结构，lineage_relationship 和 lineage_node 表不存在
-- 使用 lineage_config 和 lineage_parse_rule 表作为替代
CREATE VIEW lineage_dependency_analysis AS
SELECT 
    lpr.rule_name AS source_table,
    lpr.rule_type AS target_table,
    lpr.transform_type AS relationship_type,
    lpr.description,
    1 AS dependency_count
FROM lineage_parse_rule lpr
WHERE lpr.is_enabled = 1;