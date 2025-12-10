package com.dataplatform.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.metadata.entity.TableInfo;
import com.dataplatform.metadata.dto.TableQueryDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TableInfoMapper extends BaseMapper<TableInfo> {
    
    List<TableInfo> selectByCondition(@Param("query") TableQueryDTO queryDTO);
}