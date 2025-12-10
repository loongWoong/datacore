package com.dataplatform.lineage.service;

import com.dataplatform.lineage.dto.LineageGraphDTO;
import com.dataplatform.lineage.entity.nodes.TableNode;
import java.util.List;

public interface LineageService {
    
    /**
     * 获取血缘关系图
     */
    LineageGraphDTO getLineageGraph(String tableName, Integer depth);
    
    /**
     * 获取上游血缘关系
     */
    List<TableNode> getUpstreamLineage(String tableName, Integer depth);
    
    /**
     * 获取下游血缘关系
     */
    List<TableNode> getDownstreamLineage(String tableName, Integer depth);
}