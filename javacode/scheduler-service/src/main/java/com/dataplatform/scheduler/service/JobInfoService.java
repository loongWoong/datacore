package com.dataplatform.scheduler.service;

import com.dataplatform.scheduler.dto.*;

public interface JobInfoService {
    
    JobInfoDTO createJob(CreateJobDTO createDTO);
    
    PageResult<JobInfoDTO> getJobs(JobInfoQuery query);
    
    JobInfoDetailDTO getJobDetail(Integer id);
    
    JobInfoDTO updateJob(Integer id, UpdateJobDTO updateDTO);
    
    void deleteJob(Integer id);
    
    void startJob(Integer id);
    
    void stopJob(Integer id);
}