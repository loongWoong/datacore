package com.dataplatform.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.asset.entity.AssetTag;
import com.dataplatform.asset.entity.AssetTagRelation;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface AssetTagRelationMapper extends BaseMapper<AssetTagRelation> {
    
    List<AssetTag> selectTagsByAssetId(@Param("assetId") Long assetId);
    
    void deleteByAssetIdAndTagId(@Param("assetId") Long assetId, @Param("tagId") Long tagId);
}