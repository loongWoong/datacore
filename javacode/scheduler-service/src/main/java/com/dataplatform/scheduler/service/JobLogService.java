package com.dataplatform.scheduler.service;

import com.dataplatform.scheduler.dto.*;

public interface JobLogService {
    
    PageResult<JobLogDTO> getJobLogs(JobLogQuery query);
    
    JobLogDTO getJobLogDetail(Long id);
    
    void clearJobLogs(Integer jobId);
}