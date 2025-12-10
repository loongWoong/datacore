package com.dataplatform.quality.service;

import com.dataplatform.quality.dto.*;

public interface QualityCheckJobService {
    
    /**
     * 创建检查任务
     */
    QualityCheckJobDTO createJob(CreateJobDTO createDTO);
    
    /**
     * 获取任务列表
     */
    PageResult<QualityCheckJobDTO> getJobs(String jobName, Integer pageNum, Integer pageSize);
    
    /**
     * 触发任务执行
     */
    TriggerResultDTO triggerJob(Long id);
    
    /**
     * 启用/禁用任务
     */
    void toggleJob(Long id, Boolean enabled);
}