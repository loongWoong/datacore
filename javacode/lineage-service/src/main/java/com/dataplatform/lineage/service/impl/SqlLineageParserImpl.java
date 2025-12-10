package com.dataplatform.lineage.service.impl;

import com.dataplatform.lineage.dto.LineageGraphDTO;
import com.dataplatform.lineage.service.SqlLineageParser;
import org.springframework.stereotype.Service;

@Service
public class SqlLineageParserImpl implements SqlLineageParser {
    
    @Override
    public LineageGraphDTO parseSql(String sql, String tableName) {
        // TODO: 实现SQL血缘解析逻辑
        return new LineageGraphDTO();
    }
}