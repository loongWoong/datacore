package com.dataplatform.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.asset.entity.AssetEvaluation;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AssetEvaluationMapper extends BaseMapper<AssetEvaluation> {
    
    AssetEvaluation selectLatestByAssetId(@Param("assetId") Long assetId);
    
    List<AssetEvaluation> selectByAssetId(@Param("assetId") Long assetId);
}