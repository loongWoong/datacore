package com.dataplatform.metadata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.metadata.entity.VersionInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface VersionInfoMapper extends BaseMapper<VersionInfo> {
    
    List<VersionInfo> selectByTableId(@Param("tableId") Long tableId);
}