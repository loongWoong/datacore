package com.dataplatform.asset.controller;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.service.AssetTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets/tags")
@Tag(name = "资产标签", description = "数据资产标签管理接口")
public class AssetTagController {
    
    @Autowired
    private AssetTagService assetTagService;
    
    /**
     * 创建标签
     */
    @PostMapping
    @Operation(summary = "创建标签", description = "创建新的资产标签")
    public ResponseEntity<AssetTagDTO> createTag(
            @Parameter(description = "创建标签信息") @RequestBody CreateTagDTO createDTO) {
        AssetTagDTO tag = assetTagService.createTag(createDTO);
        return ResponseEntity.ok(tag);
    }
    
    /**
     * 获取标签列表
     */
    @GetMapping
    @Operation(summary = "获取标签列表", description = "根据分类查询资产标签列表")
    public ResponseEntity<List<AssetTagDTO>> getTags(
            @Parameter(description = "标签分类") @RequestParam(required = false) String tagCategory) {
        List<AssetTagDTO> tags = assetTagService.getTags(tagCategory);
        return ResponseEntity.ok(tags);
    }
    
    /**
     * 为资产打标签
     */
    @PostMapping("/{assetId}/tags")
    @Operation(summary = "为资产打标签", description = "为指定资产添加标签")
    public ResponseEntity<Void> tagAsset(
            @Parameter(description = "资产ID") @PathVariable Long assetId,
            @Parameter(description = "标签信息") @RequestBody TagAssetDTO tagAssetDTO) {
        assetTagService.tagAsset(assetId, tagAssetDTO.getTagIds());
        return ResponseEntity.ok().build();
    }
    
    /**
     * 移除资产标签
     */
    @DeleteMapping("/{assetId}/tags/{tagId}")
    @Operation(summary = "移除资产标签", description = "移除指定资产的标签")
    public ResponseEntity<Void> untagAsset(
            @Parameter(description = "资产ID") @PathVariable Long assetId,
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        assetTagService.untagAsset(assetId, tagId);
        return ResponseEntity.noContent().build();
    }
}