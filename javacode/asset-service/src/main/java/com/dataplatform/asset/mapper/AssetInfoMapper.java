package com.dataplatform.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.asset.entity.AssetInfo;
import com.dataplatform.asset.entity.AssetTagRelation;
import com.dataplatform.asset.dto.AssetQueryDTO;
import com.dataplatform.asset.dto.AdvancedSearchDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AssetInfoMapper extends BaseMapper<AssetInfo> {
    
    AssetInfo selectByAssetCode(@Param("assetCode") String assetCode);
    
    List<AssetInfo> selectByCondition(@Param("query") AssetQueryDTO queryDTO);
    
    List<AssetInfo> selectListByKeyword(@Param("keyword") String keyword);
    
    List<AssetInfo> selectByAdvancedCondition(@Param("search") AdvancedSearchDTO searchDTO);
    
    AssetTagRelation selectByAssetIdAndTagId(@Param("assetId") Long assetId, @Param("tagId") Long tagId);
}