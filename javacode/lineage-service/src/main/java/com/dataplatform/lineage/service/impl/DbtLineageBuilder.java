package com.dataplatform.lineage.service.impl;

import com.dataplatform.lineage.service.LineageBuilder;
import org.springframework.stereotype.Service;

@Service
public class DbtLineageBuilder implements LineageBuilder {
    
    @Override
    public void buildLineageFromDbt(String projectPath) {
        // TODO: 实现dbt模型解析和血缘关系构建逻辑
    }
}