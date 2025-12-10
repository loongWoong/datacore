package com.dataplatform.report.service;

import com.dataplatform.report.dto.*;
import org.springframework.core.io.Resource;

public interface ReportGenerationService {
    
    ReportGenerationResultDTO generateReport(GenerateReportDTO generateDTO);
    
    PageResult<ReportGenerationRecordDTO> getGenerationRecords(GenerationRecordQuery query);
    
    Resource downloadReport(Long recordId);
}