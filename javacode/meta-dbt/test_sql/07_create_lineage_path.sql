-- 创建血缘路径分析存储过程，用于查找表的上下游关系
DELIMITER //

CREATE PROCEDURE GetTableLineagePath(
    IN start_table VARCHAR(255),
    IN max_depth INT
)
BEGIN
    DECLARE current_depth INT DEFAULT 1;
    
    -- 创建临时表存储结果
    CREATE TEMPORARY TABLE temp_lineage_path (
        table_name VARCHAR(255),
        related_table VARCHAR(255),
        relationship_type VARCHAR(50),
        depth INT
    );
    
    -- 插入起始表的直接关系
    INSERT INTO temp_lineage_path
    SELECT 
        ln1.table_name,
        ln2.table_name,
        lr.relationship_type,
        current_depth
    FROM lineage_relationship lr
    JOIN lineage_node ln1 ON lr.source_node_id = ln1.id
    JOIN lineage_node ln2 ON lr.target_node_id = ln2.id
    WHERE ln1.table_name = start_table OR ln2.table_name = start_table;
    
    -- 循环查找间接关系
    WHILE current_depth < max_depth DO
        SET current_depth = current_depth + 1;
        
        INSERT INTO temp_lineage_path
        SELECT 
            tlp.related_table,
            ln2.table_name,
            lr.relationship_type,
            current_depth
        FROM temp_lineage_path tlp
        JOIN lineage_relationship lr ON tlp.related_table = (
            SELECT ln.table_name 
            FROM lineage_node ln 
            WHERE ln.id = lr.source_node_id
        )
        JOIN lineage_node ln2 ON lr.target_node_id = ln2.id
        WHERE tlp.depth = current_depth - 1
        AND NOT EXISTS (
            SELECT 1 
            FROM temp_lineage_path tlp2 
            WHERE tlp2.table_name = tlp.related_table 
            AND tlp2.related_table = ln2.table_name
        );
    END WHILE;
    
    -- 返回结果
    SELECT * FROM temp_lineage_path;
    
    -- 清理临时表
    DROP TEMPORARY TABLE temp_lineage_path;
END //

DELIMITER ;