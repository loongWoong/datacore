package com.dataplatform.scheduler.service;

import com.dataplatform.scheduler.dto.*;

public interface JobGroupService {
    
    JobGroupDTO createJobGroup(CreateJobGroupDTO createDTO);
    
    PageResult<JobGroupDTO> getJobGroups();
    
    JobGroupDTO getJobGroupDetail(Integer id);
    
    JobGroupDTO updateJobGroup(Integer id, UpdateJobGroupDTO updateDTO);
    
    void deleteJobGroup(Integer id);
}