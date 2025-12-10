package com.dataplatform.scheduler.service.impl;

import com.dataplatform.scheduler.dto.*;
import com.dataplatform.scheduler.entity.JobInfo;
import com.dataplatform.scheduler.mapper.JobInfoMapper;
import com.dataplatform.scheduler.service.JobInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobInfoServiceImpl implements JobInfoService {
    
    @Autowired
    private JobInfoMapper jobInfoMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public JobInfoDTO createJob(CreateJobDTO createDTO) {
        // 1. 创建任务信息
        JobInfo jobInfo = new JobInfo();
        BeanUtils.copyProperties(createDTO, jobInfo);
        jobInfo.setAddTime(new Date());
        jobInfo.setUpdateTime(new Date());
        jobInfo.setTriggerStatus(0); // 默认停止状态
        jobInfo.setMisfireStrategy("DO_NOTHING"); // 默认调度过期策略
        jobInfo.setGlueType("BEAN"); // 默认GLUE类型
        jobInfoMapper.insert(jobInfo);
        
        // 2. 清除缓存
        redisTemplate.delete("scheduler:jobs");
        
        // 3. 返回结果
        JobInfoDTO result = new JobInfoDTO();
        BeanUtils.copyProperties(jobInfo, result);
        return result;
    }
    
    @Override
    public PageResult<JobInfoDTO> getJobs(JobInfoQuery query) {
        // 1. 尝试从缓存获取
        String cacheKey = "scheduler:jobs:" + com.alibaba.fastjson.JSON.toJSONString(query);
        PageResult<JobInfoDTO> cachedResult = (PageResult<JobInfoDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<JobInfo> jobs = jobInfoMapper.selectByCondition(query);
        
        List<JobInfoDTO> dtos = jobs.stream()
                .map(job -> {
                    JobInfoDTO dto = new JobInfoDTO();
                    BeanUtils.copyProperties(job, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<JobInfo> pageInfo = new PageInfo<>(jobs);
        PageResult<JobInfoDTO> result = PageResult.of(dtos, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
    
    @Override
    public JobInfoDetailDTO getJobDetail(Integer id) {
        // 1. 查询任务信息
        JobInfo jobInfo = jobInfoMapper.selectById(id);
        if (jobInfo == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 2. 构造详细信息DTO
        JobInfoDetailDTO detailDTO = new JobInfoDetailDTO();
        BeanUtils.copyProperties(jobInfo, detailDTO);
        
        return detailDTO;
    }
    
    @Override
    @Transactional
    public JobInfoDTO updateJob(Integer id, UpdateJobDTO updateDTO) {
        JobInfo existingJob = jobInfoMapper.selectById(id);
        if (existingJob == null) {
            throw new RuntimeException("任务不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingJob);
        existingJob.setUpdateTime(new Date());
        jobInfoMapper.updateById(existingJob);
        
        // 清除缓存
        redisTemplate.delete("scheduler:jobs");
        redisTemplate.delete("scheduler:job:" + id);
        
        JobInfoDTO result = new JobInfoDTO();
        BeanUtils.copyProperties(existingJob, result);
        return result;
    }
    
    @Override
    @Transactional
    public void deleteJob(Integer id) {
        JobInfo existingJob = jobInfoMapper.selectById(id);
        if (existingJob == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 删除任务
        jobInfoMapper.deleteById(id);
        
        // 清除缓存
        redisTemplate.delete("scheduler:jobs");
        redisTemplate.delete("scheduler:job:" + id);
    }
    
    @Override
    @Transactional
    public void startJob(Integer id) {
        JobInfo existingJob = jobInfoMapper.selectById(id);
        if (existingJob == null) {
            throw new RuntimeException("任务不存在");
        }
        
        existingJob.setTriggerStatus(1); // 设置为运行状态
        existingJob.setUpdateTime(new Date());
        jobInfoMapper.updateById(existingJob);
        
        // 清除缓存
        redisTemplate.delete("scheduler:jobs");
        redisTemplate.delete("scheduler:job:" + id);
    }
    
    @Override
    @Transactional
    public void stopJob(Integer id) {
        JobInfo existingJob = jobInfoMapper.selectById(id);
        if (existingJob == null) {
            throw new RuntimeException("任务不存在");
        }
        
        existingJob.setTriggerStatus(0); // 设置为停止状态
        existingJob.setUpdateTime(new Date());
        jobInfoMapper.updateById(existingJob);
        
        // 清除缓存
        redisTemplate.delete("scheduler:jobs");
        redisTemplate.delete("scheduler:job:" + id);
    }
}