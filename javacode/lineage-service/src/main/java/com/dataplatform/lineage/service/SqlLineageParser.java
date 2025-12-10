package com.dataplatform.lineage.service;

import com.dataplatform.lineage.dto.LineageGraphDTO;

public interface SqlLineageParser {
    
    /**
     * 解析SQL血缘关系
     */
    LineageGraphDTO parseSql(String sql, String tableName);
}