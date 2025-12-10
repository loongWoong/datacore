package com.dataplatform.report.service;

import com.dataplatform.report.dto.*;

public interface ReportConfigService {
    
    ReportConfigDTO createReportConfig(CreateReportConfigDTO createDTO);
    
    PageResult<ReportConfigDTO> getReportConfigs(ReportConfigQuery query);
    
    ReportConfigDetailDTO getReportConfigDetail(Long id);
    
    ReportConfigDTO updateReportConfig(Long id, UpdateReportConfigDTO updateDTO);
    
    void deleteReportConfig(Long id);
}