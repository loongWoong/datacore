package com.dataplatform.asset.controller;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.service.AssetSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets/search")
@Tag(name = "资产搜索", description = "数据资产搜索接口")
public class AssetSearchController {
    
    @Autowired
    private AssetSearchService assetSearchService;
    
    /**
     * 全文搜索资产
     */
    @GetMapping
    @Operation(summary = "全文搜索资产", description = "根据关键字全文搜索数据资产")
    public ResponseEntity<PageResult<AssetInfoDTO>> searchAssets(
            @Parameter(description = "搜索关键字") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        PageResult<AssetInfoDTO> pageResult = assetSearchService.searchAssets(keyword, pageNum, pageSize);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 高级搜索资产
     */
    @PostMapping("/advanced")
    @Operation(summary = "高级搜索资产", description = "根据条件高级搜索数据资产")
    public ResponseEntity<PageResult<AssetInfoDTO>> advancedSearch(
            @Parameter(description = "高级搜索条件") @RequestBody AdvancedSearchDTO searchDTO) {
        PageResult<AssetInfoDTO> pageResult = assetSearchService.advancedSearch(searchDTO);
        return ResponseEntity.ok(pageResult);
    }
}