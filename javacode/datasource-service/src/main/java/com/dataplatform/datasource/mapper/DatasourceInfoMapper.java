package com.dataplatform.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.datasource.entity.DatasourceInfo;
import org.apache.ibatis.annotations.Param;

public interface DatasourceInfoMapper extends BaseMapper<DatasourceInfo> {
    
    /**
     * 分页查询数据源列表
     */
    IPage<DatasourceInfo> selectDatasourcePage(Page<DatasourceInfo> page, @Param("query") DatasourceInfo query);
}