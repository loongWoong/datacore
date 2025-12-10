package com.dataplatform.metadata.controller;

import com.dataplatform.metadata.dto.CreateVersionDTO;
import com.dataplatform.metadata.dto.VersionInfoDTO;
import com.dataplatform.metadata.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata/versions")
@Tag(name = "版本管理", description = "元数据版本管理接口")
public class VersionController {
    
    @Autowired
    private VersionService versionService;
    
    /**
     * 获取表的版本列表
     */
    @GetMapping("/table/{tableId}")
    @Operation(summary = "获取表的版本列表", description = "根据表ID查询版本列表")
    public ResponseEntity<List<VersionInfoDTO>> getVersionsByTable(
            @Parameter(description = "表ID") @PathVariable Long tableId) {
        List<VersionInfoDTO> versions = versionService.getVersionsByTable(tableId);
        return ResponseEntity.ok(versions);
    }
    
    /**
     * 创建版本元数据
     */
    @PostMapping
    @Operation(summary = "创建版本元数据", description = "创建新的版本元数据信息")
    public ResponseEntity<VersionInfoDTO> createVersion(
            @Parameter(description = "版本信息") @RequestBody CreateVersionDTO createDTO) {
        VersionInfoDTO versionInfo = versionService.createVersion(createDTO);
        return ResponseEntity.ok(versionInfo);
    }
}