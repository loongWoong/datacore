package com.dataplatform.asset.service;

import com.dataplatform.asset.dto.*;

import java.util.List;

public interface AssetTagService {
    
    /**
     * 创建标签
     */
    AssetTagDTO createTag(CreateTagDTO createDTO);
    
    /**
     * 获取标签列表
     */
    List<AssetTagDTO> getTags(String tagCategory);
    
    /**
     * 为资产打标签
     */
    void tagAsset(Long assetId, List<Long> tagIds);
    
    /**
     * 移除资产标签
     */
    void untagAsset(Long assetId, Long tagId);
}