package com.dataplatform.asset.service;

import com.dataplatform.asset.dto.*;

public interface AssetService {
    
    /**
     * 创建资产
     */
    AssetInfoDTO createAsset(CreateAssetDTO createDTO);
    
    /**
     * 获取资产列表
     */
    PageResult<AssetInfoDTO> getAssets(AssetQueryDTO queryDTO);
    
    /**
     * 获取资产详情
     */
    AssetDetailDTO getAssetDetail(Long id);
    
    /**
     * 更新资产
     */
    AssetInfoDTO updateAsset(Long id, UpdateAssetDTO updateDTO);
}