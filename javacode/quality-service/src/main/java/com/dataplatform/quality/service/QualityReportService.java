package com.dataplatform.quality.service;

import com.dataplatform.quality.dto.*;

import java.util.List;

public interface QualityReportService {
    
    /**
     * 获取质量报告列表
     */
    PageResult<QualityReportDTO> getReports(QualityReportQuery query);
    
    /**
     * 获取表的质量趋势
     */
    List<QualityTrendDTO> getQualityTrend(String tableName);
}