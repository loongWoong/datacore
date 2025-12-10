package com.dataplatform.asset.controller;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "资产管理", description = "数据资产管理接口")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    /**
     * 创建资产
     */
    @PostMapping
    @Operation(summary = "创建资产", description = "创建新的数据资产")
    public ResponseEntity<AssetInfoDTO> createAsset(
            @Parameter(description = "创建资产信息") @RequestBody CreateAssetDTO createDTO) {
        AssetInfoDTO asset = assetService.createAsset(createDTO);
        return ResponseEntity.ok(asset);
    }
    
    /**
     * 获取资产列表
     */
    @GetMapping
    @Operation(summary = "获取资产列表", description = "根据条件查询数据资产列表")
    public ResponseEntity<PageResult<AssetInfoDTO>> getAssets(
            @Parameter(description = "资产类型") @RequestParam(required = false) String assetType,
            @Parameter(description = "业务域") @RequestParam(required = false) String businessDomain,
            @Parameter(description = "负责人") @RequestParam(required = false) String owner,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String search,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        AssetQueryDTO queryDTO = AssetQueryDTO.builder()
                .assetType(assetType)
                .businessDomain(businessDomain)
                .owner(owner)
                .search(search)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
                
        PageResult<AssetInfoDTO> pageResult = assetService.getAssets(queryDTO);
        return ResponseEntity.ok(pageResult);
    }
    
    /**
     * 获取资产详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取资产详情", description = "根据ID获取数据资产详情")
    public ResponseEntity<AssetDetailDTO> getAssetDetail(
            @Parameter(description = "资产ID") @PathVariable Long id) {
        AssetDetailDTO assetDetail = assetService.getAssetDetail(id);
        return ResponseEntity.ok(assetDetail);
    }
    
    /**
     * 更新资产
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新资产", description = "更新数据资产信息")
    public ResponseEntity<AssetInfoDTO> updateAsset(
            @Parameter(description = "资产ID") @PathVariable Long id,
            @Parameter(description = "更新资产信息") @RequestBody UpdateAssetDTO updateDTO) {
        AssetInfoDTO asset = assetService.updateAsset(id, updateDTO);
        return ResponseEntity.ok(asset);
    }
}