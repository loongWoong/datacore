package com.dataplatform.metadata.controller;

import com.dataplatform.metadata.dto.CreateColumnDTO;
import com.dataplatform.metadata.dto.ColumnInfoDTO;
import com.dataplatform.metadata.service.ColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata/columns")
@Tag(name = "字段管理", description = "字段元数据管理接口")
public class ColumnController {
    
    @Autowired
    private ColumnService columnService;
    
    /**
     * 获取表的字段列表
     */
    @GetMapping("/table/{tableId}")
    @Operation(summary = "获取表的字段列表", description = "根据表ID查询字段列表")
    public ResponseEntity<List<ColumnInfoDTO>> getColumnsByTable(
            @Parameter(description = "表ID") @PathVariable Long tableId) {
        List<ColumnInfoDTO> columns = columnService.getColumnsByTable(tableId);
        return ResponseEntity.ok(columns);
    }
    
    /**
     * 创建字段元数据
     */
    @PostMapping
    @Operation(summary = "创建字段元数据", description = "创建新的字段元数据信息")
    public ResponseEntity<ColumnInfoDTO> createColumn(
            @Parameter(description = "字段信息") @RequestBody CreateColumnDTO createDTO) {
        ColumnInfoDTO columnInfo = columnService.createColumn(createDTO);
        return ResponseEntity.ok(columnInfo);
    }
}