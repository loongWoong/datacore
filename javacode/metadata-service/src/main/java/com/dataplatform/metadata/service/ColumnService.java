package com.dataplatform.metadata.service;

import com.dataplatform.metadata.dto.CreateColumnDTO;
import com.dataplatform.metadata.dto.ColumnInfoDTO;
import java.util.List;

public interface ColumnService {
    
    /**
     * 创建字段元数据
     */
    ColumnInfoDTO createColumn(CreateColumnDTO createDTO);
    
    /**
     * 获取表的字段列表
     */
    List<ColumnInfoDTO> getColumnsByTable(Long tableId);
}