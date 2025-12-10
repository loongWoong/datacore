package com.dataplatform.lineage.controller;

import com.dataplatform.lineage.dto.DbtBuildRequestDTO;
import com.dataplatform.lineage.dto.LineageRelationshipDTO;
import com.dataplatform.lineage.service.LineageBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lineage/build")
@Tag(name = "血缘构建", description = "数据血缘构建接口")
public class LineageBuildController {
    
    @Autowired
    private LineageBuilder lineageBuilder;
    
    /**
     * 触发dbt血缘构建
     */
    @PostMapping("/dbt")
    @Operation(summary = "触发dbt血缘构建", description = "根据dbt项目路径构建血缘关系")
    public ResponseEntity<String> buildLineageFromDbt(
            @Parameter(description = "dbt构建请求") @RequestBody DbtBuildRequestDTO request) {
        
        lineageBuilder.buildLineageFromDbt(request.getProjectPath());
        return ResponseEntity.ok("血缘关系构建成功");
    }
    
    /**
     * 手动添加血缘关系
     */
    @PostMapping("/relationship")
    @Operation(summary = "手动添加血缘关系", description = "手动添加表之间的血缘关系")
    public ResponseEntity<String> addLineageRelationship(
            @Parameter(description = "血缘关系信息") @RequestBody LineageRelationshipDTO relationship) {
        
        // TODO: 实现手动添加血缘关系的逻辑
        return ResponseEntity.ok("血缘关系添加成功");
    }
}