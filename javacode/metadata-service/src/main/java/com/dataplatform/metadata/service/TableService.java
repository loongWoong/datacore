package com.dataplatform.metadata.service;

import com.dataplatform.metadata.dto.CreateTableDTO;
import com.dataplatform.metadata.dto.TableDetailDTO;
import com.dataplatform.metadata.dto.TableInfoDTO;
import com.dataplatform.metadata.dto.TableQueryDTO;
import java.util.List;

public interface TableService {
    
    /**
     * 创建表元数据
     */
    TableInfoDTO createTable(CreateTableDTO createDTO);
    
    /**
     * 获取表列表
     */
    List<TableInfoDTO> getTables(TableQueryDTO queryDTO);
    
    /**
     * 获取表详情
     */
    TableDetailDTO getTableDetail(Long id);
}