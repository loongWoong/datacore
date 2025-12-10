package com.dataplatform.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.asset.entity.AssetTag;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AssetTagMapper extends BaseMapper<AssetTag> {
    
    List<AssetTag> selectByCategory(@Param("tagCategory") String tagCategory);
}