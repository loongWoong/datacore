package com.dataplatform.metadata.controller;

import com.dataplatform.metadata.dto.CreateTableDTO;
import com.dataplatform.metadata.dto.TableDetailDTO;
import com.dataplatform.metadata.dto.TableInfoDTO;
import com.dataplatform.metadata.dto.TableQueryDTO;
import com.dataplatform.metadata.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metadata/tables")
@Tag(name = "表管理", description = "表元数据管理接口")
public class TableController {
    
    @Autowired
    private TableService tableService;
    
    /**
     * 获取表列表
     */
    @GetMapping
    @Operation(summary = "获取表列表", description = "根据条件查询表列表")
    public ResponseEntity<List<TableInfoDTO>> getTables(
            @Parameter(description = "数据分层") @RequestParam(required = false) String layer,
            @Parameter(description = "业务域") @RequestParam(required = false) String businessDomain,
            @Parameter(description = "负责人") @RequestParam(required = false) String owner,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String search) {
        
        TableQueryDTO queryDTO = TableQueryDTO.builder()
                .layer(layer)
                .businessDomain(businessDomain)
                .owner(owner)
                .search(search)
                .build();
                
        List<TableInfoDTO> tables = tableService.getTables(queryDTO);
        return ResponseEntity.ok(tables);
    }
    
    /**
     * 获取表详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取表详情", description = "根据表ID获取表详细信息")
    public ResponseEntity<TableDetailDTO> getTableDetail(
            @Parameter(description = "表ID") @PathVariable Long id) {
        TableDetailDTO tableDetail = tableService.getTableDetail(id);
        return ResponseEntity.ok(tableDetail);
    }
    
    /**
     * 创建表元数据
     */
    @PostMapping
    @Operation(summary = "创建表元数据", description = "创建新的表元数据信息")
    public ResponseEntity<TableInfoDTO> createTable(
            @Parameter(description = "表信息") @RequestBody CreateTableDTO createDTO) {
        TableInfoDTO tableInfo = tableService.createTable(createDTO);
        return ResponseEntity.ok(tableInfo);
    }
}