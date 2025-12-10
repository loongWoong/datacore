package com.dataplatform.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.asset.entity.AssetLifecycle;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AssetLifecycleMapper extends BaseMapper<AssetLifecycle> {
    
    List<AssetLifecycle> selectByAssetId(@Param("assetId") Long assetId);
}