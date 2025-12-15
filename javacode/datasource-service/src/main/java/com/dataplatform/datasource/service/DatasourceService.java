package com.dataplatform.datasource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataplatform.datasource.dto.CreateDatasourceDTO;
import com.dataplatform.datasource.dto.DatasourceInfoDTO;
import com.dataplatform.datasource.dto.DatasourceQueryDTO;
import com.dataplatform.datasource.dto.UpdateDatasourceDTO;

import java.util.List;

public interface DatasourceService {
    
    /**
     * 获取数据源列表
     */
    List<DatasourceInfoDTO> getDatasources(DatasourceQueryDTO queryDTO);
    
    /**
     * 分页获取数据源列表
     */
    IPage<DatasourceInfoDTO> getDatasourcePage(int pageNum, int pageSize, DatasourceQueryDTO queryDTO);
    
    /**
     * 获取数据源详情
     */
    DatasourceInfoDTO getDatasourceDetail(Long id);
    
    /**
     * 创建数据源
     */
    DatasourceInfoDTO createDatasource(CreateDatasourceDTO createDTO);
    
    /**
     * 更新数据源
     */
    DatasourceInfoDTO updateDatasource(Long id, UpdateDatasourceDTO updateDTO);
    
    /**
     * 删除数据源
     */
    boolean deleteDatasource(Long id);
    
    /**
     * 测试数据源连接
     */
    boolean testConnection(Long id);
}