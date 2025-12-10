package com.dataplatform.quality.service.impl;

import com.dataplatform.quality.dto.*;
import com.dataplatform.quality.service.QualityReportService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QualityReportServiceImpl implements QualityReportService {
    
    @Override
    public PageResult<QualityReportDTO> getReports(QualityReportQuery query) {
        // TODO: 实现质量报告列表查询逻辑
        return PageResult.of(new ArrayList<>(), 0, query.getPageNum(), query.getPageSize());
    }
    
    @Override
    public List<QualityTrendDTO> getQualityTrend(String tableName) {
        // TODO: 实现质量趋势查询逻辑
        return new ArrayList<>();
    }
}