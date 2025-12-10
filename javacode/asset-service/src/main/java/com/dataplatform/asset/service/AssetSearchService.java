package com.dataplatform.asset.service;

import com.dataplatform.asset.dto.*;

public interface AssetSearchService {
    
    /**
     * 全文搜索资产
     */
    PageResult<AssetInfoDTO> searchAssets(String keyword, Integer pageNum, Integer pageSize);
    
    /**
     * 高级搜索资产
     */
    PageResult<AssetInfoDTO> advancedSearch(AdvancedSearchDTO searchDTO);
}