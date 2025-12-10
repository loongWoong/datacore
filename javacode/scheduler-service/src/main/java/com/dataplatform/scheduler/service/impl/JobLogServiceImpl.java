package com.dataplatform.scheduler.service.impl;

import com.dataplatform.scheduler.dto.*;
import com.dataplatform.scheduler.entity.JobLog;
import com.dataplatform.scheduler.mapper.JobLogMapper;
import com.dataplatform.scheduler.service.JobLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobLogServiceImpl implements JobLogService {
    
    @Autowired
    private JobLogMapper jobLogMapper;
    
    @Override
    public PageResult<JobLogDTO> getJobLogs(JobLogQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<JobLog> logs = jobLogMapper.selectByCondition(query);
        
        List<JobLogDTO> dtos = logs.stream()
                .map(log -> {
                    JobLogDTO dto = new JobLogDTO();
                    BeanUtils.copyProperties(log, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<JobLog> pageInfo = new PageInfo<>(logs);
        return PageResult.of(dtos, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }
    
    @Override
    public JobLogDTO getJobLogDetail(Long id) {
        JobLog jobLog = jobLogMapper.selectById(id);
        if (jobLog == null) {
            throw new RuntimeException("任务日志不存在");
        }
        
        JobLogDTO result = new JobLogDTO();
        BeanUtils.copyProperties(jobLog, result);
        return result;
    }
    
    @Override
    public void clearJobLogs(Integer jobId) {
        // 实际项目中应实现日志清理逻辑
        System.out.println("清理任务日志: jobId=" + jobId);
    }
}