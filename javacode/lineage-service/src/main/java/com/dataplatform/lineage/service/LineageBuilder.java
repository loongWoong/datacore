package com.dataplatform.lineage.service;

public interface LineageBuilder {
    
    /**
     * 从dbt构建血缘关系
     */
    void buildLineageFromDbt(String projectPath);
}