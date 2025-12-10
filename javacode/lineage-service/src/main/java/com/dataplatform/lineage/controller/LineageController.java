package com.dataplatform.lineage.controller;

import com.dataplatform.lineage.dto.LineageGraphDTO;
import com.dataplatform.lineage.dto.LineageNodeDTO;
import com.dataplatform.lineage.entity.nodes.TableNode;
import com.dataplatform.lineage.service.LineageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lineage")
@Tag(name = "血缘管理", description = "数据血缘管理接口")
public class LineageController {
    
    @Autowired
    private LineageService lineageService;
    
    /**
     * 获取血缘关系图
     */
    @GetMapping("/graph")
    @Operation(summary = "获取血缘关系图", description = "根据表名和深度获取血缘关系图")
    public ResponseEntity<LineageGraphDTO> getLineageGraph(
            @Parameter(description = "表名") @RequestParam(required = false) String tableName,
            @Parameter(description = "血缘深度") @RequestParam(defaultValue = "3") Integer depth) {
        
        LineageGraphDTO graph = lineageService.getLineageGraph(tableName, depth);
        return ResponseEntity.ok(graph);
    }
    
    /**
     * 获取上游血缘关系
     */
    @GetMapping("/upstream/{tableName}")
    @Operation(summary = "获取上游血缘关系", description = "根据表名和深度获取上游血缘关系")
    public ResponseEntity<List<TableNode>> getUpstreamLineage(
            @Parameter(description = "表名") @PathVariable String tableName,
            @Parameter(description = "血缘深度") @RequestParam(defaultValue = "3") Integer depth) {
        
        List<TableNode> upstreamNodes = lineageService.getUpstreamLineage(tableName, depth);
        return ResponseEntity.ok(upstreamNodes);
    }
    
    /**
     * 获取下游血缘关系
     */
    @GetMapping("/downstream/{tableName}")
    @Operation(summary = "获取下游血缘关系", description = "根据表名和深度获取下游血缘关系")
    public ResponseEntity<List<TableNode>> getDownstreamLineage(
            @Parameter(description = "表名") @PathVariable String tableName,
            @Parameter(description = "血缘深度") @RequestParam(defaultValue = "3") Integer depth) {
        
        List<TableNode> downstreamNodes = lineageService.getDownstreamLineage(tableName, depth);
        return ResponseEntity.ok(downstreamNodes);
    }
}