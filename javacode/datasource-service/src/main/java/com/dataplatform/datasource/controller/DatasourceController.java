package com.dataplatform.datasource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataplatform.datasource.dto.CreateDatasourceDTO;
import com.dataplatform.datasource.dto.DatasourceInfoDTO;
import com.dataplatform.datasource.dto.DatasourceQueryDTO;
import com.dataplatform.datasource.dto.UpdateDatasourceDTO;
import com.dataplatform.datasource.service.DatasourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datasources")
@Tag(name = "数据源管理", description = "数据源管理接口")
public class DatasourceController {
    
    @Autowired
    private DatasourceService datasourceService;
    
    /**
     * 获取数据源列表
     */
    @GetMapping
    @Operation(summary = "获取数据源列表", description = "根据条件查询数据源列表")
    public ResponseEntity<List<DatasourceInfoDTO>> getDatasources(
            @Parameter(description = "数据源名称") @RequestParam(required = false) String name,
            @Parameter(description = "数据源类型") @RequestParam(required = false) String type,
            @Parameter(description = "负责人") @RequestParam(required = false) String owner,
            @Parameter(description = "环境") @RequestParam(required = false) String environment,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String search) {
        
        DatasourceQueryDTO queryDTO = DatasourceQueryDTO.builder()
                .name(name)
                .type(type)
                .owner(owner)
                .environment(environment)
                .status(status)
                .search(search)
                .build();
                
        List<DatasourceInfoDTO> datasources = datasourceService.getDatasources(queryDTO);
        return ResponseEntity.ok(datasources);
    }
    
    /**
     * 分页获取数据源列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取数据源列表", description = "分页查询数据源列表")
    public ResponseEntity<IPage<DatasourceInfoDTO>> getDatasourcePage(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "数据源名称") @RequestParam(required = false) String name,
            @Parameter(description = "数据源类型") @RequestParam(required = false) String type,
            @Parameter(description = "负责人") @RequestParam(required = false) String owner,
            @Parameter(description = "环境") @RequestParam(required = false) String environment,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String search) {
        
        DatasourceQueryDTO queryDTO = DatasourceQueryDTO.builder()
                .name(name)
                .type(type)
                .owner(owner)
                .environment(environment)
                .status(status)
                .search(search)
                .build();
                
        IPage<DatasourceInfoDTO> datasourcePage = datasourceService.getDatasourcePage(pageNum, pageSize, queryDTO);
        return ResponseEntity.ok(datasourcePage);
    }
    
    /**
     * 获取数据源详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取数据源详情", description = "根据数据源ID获取详细信息")
    public ResponseEntity<DatasourceInfoDTO> getDatasourceDetail(
            @Parameter(description = "数据源ID") @PathVariable Long id) {
        DatasourceInfoDTO datasourceDetail = datasourceService.getDatasourceDetail(id);
        if (datasourceDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(datasourceDetail);
    }
    
    /**
     * 创建数据源
     */
    @PostMapping
    @Operation(summary = "创建数据源", description = "创建新的数据源信息")
    public ResponseEntity<DatasourceInfoDTO> createDatasource(
            @Parameter(description = "数据源信息") @RequestBody CreateDatasourceDTO createDTO) {
        DatasourceInfoDTO datasourceInfo = datasourceService.createDatasource(createDTO);
        return ResponseEntity.ok(datasourceInfo);
    }
    
    /**
     * 更新数据源
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新数据源", description = "更新数据源信息")
    public ResponseEntity<DatasourceInfoDTO> updateDatasource(
            @Parameter(description = "数据源ID") @PathVariable Long id,
            @Parameter(description = "数据源信息") @RequestBody UpdateDatasourceDTO updateDTO) {
        DatasourceInfoDTO datasourceInfo = datasourceService.updateDatasource(id, updateDTO);
        if (datasourceInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(datasourceInfo);
    }
    
    /**
     * 删除数据源
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除数据源", description = "删除数据源信息")
    public ResponseEntity<Boolean> deleteDatasource(
            @Parameter(description = "数据源ID") @PathVariable Long id) {
        boolean result = datasourceService.deleteDatasource(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试数据源连接
     */
    @PostMapping("/{id}/test-connection")
    @Operation(summary = "测试数据源连接", description = "测试数据源连接是否正常")
    public ResponseEntity<Boolean> testConnection(
            @Parameter(description = "数据源ID") @PathVariable Long id) {
        boolean result = datasourceService.testConnection(id);
        return ResponseEntity.ok(result);
    }
}