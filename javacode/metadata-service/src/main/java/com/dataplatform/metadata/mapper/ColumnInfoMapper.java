package com.dataplatform.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.metadata.entity.ColumnInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ColumnInfoMapper extends BaseMapper<ColumnInfo> {
    
    List<ColumnInfo> selectByTableId(@Param("tableId") Long tableId);
}