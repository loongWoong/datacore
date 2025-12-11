package com.dataplatform.quality.service.impl;

import com.dataplatform.quality.dto.*;
import com.dataplatform.quality.entity.QualityCheckJob;
import com.dataplatform.quality.job.QualityCheckQuartzJob;
import com.dataplatform.quality.entity.QualityCheckResult;
import com.dataplatform.quality.entity.QualityRule;
import com.dataplatform.quality.mapper.QualityCheckJobMapper;
import com.dataplatform.quality.mapper.QualityCheckResultMapper;
import com.dataplatform.quality.mapper.QualityRuleMapper;
import com.dataplatform.quality.service.QualityCheckJobService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.quartz.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QualityCheckJobServiceImpl implements QualityCheckJobService {
    
    @Autowired
    private QualityCheckJobMapper qualityCheckJobMapper;
    
    @Autowired
    private QualityRuleMapper qualityRuleMapper;
    
    @Autowired
    private QualityCheckResultMapper qualityCheckResultMapper;
    
    @Autowired
    private Scheduler scheduler;
    
    @Override
    @Transactional
    public QualityCheckJobDTO createJob(CreateJobDTO createDTO) {
        // 1. 创建任务
        QualityCheckJob job = new QualityCheckJob();
        BeanUtils.copyProperties(createDTO, job);
        job.setCreator("system"); // 实际项目中应从安全上下文中获取当前用户
        job.setCreatedTime(new Date());
        job.setUpdatedTime(new Date());
        qualityCheckJobMapper.insert(job);
        
        // 2. 注册Quartz任务
        registerQuartzJob(job);
        
        // 3. 返回结果
        QualityCheckJobDTO result = new QualityCheckJobDTO();
        BeanUtils.copyProperties(job, result);
        return result;
    }
    
    @Override
    public PageResult<QualityCheckJobDTO> getJobs(String jobName, Integer pageNum, Integer pageSize) {
        // TODO: 实现任务列表查询逻辑
        return PageResult.of(new ArrayList<>(), 0, pageNum, pageSize);
    }
    
    @Override
    public TriggerResultDTO triggerJob(Long id) {
        QualityCheckJob job = qualityCheckJobMapper.selectById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 执行质量检查
        List<QualityCheckResult> results = executeQualityCheck(job);
        
        // 保存检查结果
        for (QualityCheckResult result : results) {
            qualityCheckResultMapper.insert(result);
        }
        
        // 构造返回结果
        TriggerResultDTO dto = new TriggerResultDTO();
        dto.setSuccess(true);
        dto.setMessage("检查完成");
        dto.setResultCount(results.size());
        return dto;
    }
    
    @Override
    @Transactional
    public void toggleJob(Long id, Boolean enabled) {
        QualityCheckJob job = qualityCheckJobMapper.selectById(id);
        if (job == null) {
            throw new RuntimeException("任务不存在");
        }
        
        job.setIsEnabled(enabled ? 1 : 0);
        job.setUpdatedTime(new Date());
        qualityCheckJobMapper.updateById(job);
        
        // 更新Quartz任务状态
        updateQuartzJobStatus(job);
    }
    
    /**
     * 注册Quartz任务
     */
    private void registerQuartzJob(QualityCheckJob job) {
        try {
            // 创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(QualityCheckQuartzJob.class)
                    .withIdentity("job_" + job.getId(), "quality_group")
                    .usingJobData("jobId", job.getId())
                    .build();
            
            // 创建Trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger_" + job.getId(), "quality_group")
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .build();
            
            // 调度任务
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("注册Quartz任务失败", e);
        }
    }
    
    /**
     * 更新Quartz任务状态
     */
    private void updateQuartzJobStatus(QualityCheckJob job) {
        try {
            JobKey jobKey = new JobKey("job_" + job.getId(), "quality_group");
            if (scheduler.checkExists(jobKey)) {
                if (job.getIsEnabled() == 1) {
                    scheduler.resumeJob(jobKey);
                } else {
                    scheduler.pauseJob(jobKey);
                }
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("更新Quartz任务状态失败", e);
        }
    }
    
    /**
     * 执行质量检查
     */
    private List<QualityCheckResult> executeQualityCheck(QualityCheckJob job) {
        List<QualityCheckResult> results = new ArrayList<>();
        
        // 1. 解析规则ID列表
        List<Long> ruleIds = Arrays.stream(job.getRuleIds().split(","))
                .map(Long::valueOf)
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
        
        // 2. 获取规则详情
        List<QualityRule> rules = qualityRuleMapper.selectBatchIds(ruleIds);
        
        // 3. 执行每个规则的检查
        for (QualityRule rule : rules) {
            QualityCheckResult result = executeRuleCheck(rule);
            result.setJobId(job.getId());
            results.add(result);
        }
        
        return results;
    }
    
    /**
     * 执行单个规则检查
     */
    private QualityCheckResult executeRuleCheck(QualityRule rule) {
        QualityCheckResult result = new QualityCheckResult();
        result.setRuleId(rule.getId());
        result.setTableName(rule.getTableName());
        result.setColumnName(rule.getColumnName());
        result.setCheckDate(new Date());
        
        try {
            // 根据规则类型执行不同的检查逻辑
            switch (rule.getRuleType()) {
                case "COMPLETENESS":
                    executeCompletenessCheck(rule, result);
                    break;
                case "ACCURACY":
                    executeAccuracyCheck(rule, result);
                    break;
                case "CONSISTENCY":
                    executeConsistencyCheck(rule, result);
                    break;
                case "UNIQUENESS":
                    executeUniquenessCheck(rule, result);
                    break;
                default:
                    throw new RuntimeException("不支持的规则类型: " + rule.getRuleType());
            }
        } catch (Exception e) {
            result.setCheckStatus("FAIL");
            result.setErrorMessage(e.getMessage());
        }
        
        result.setCreatedTime(new Date());
        return result;
    }
    
    /**
     * 完整性检查
     */
    private void executeCompletenessCheck(QualityRule rule, QualityCheckResult result) {
        // TODO: 实现完整性检查逻辑
        result.setCheckStatus("PASS");
    }
    
    /**
     * 准确性检查
     */
    private void executeAccuracyCheck(QualityRule rule, QualityCheckResult result) {
        // TODO: 实现准确性检查逻辑
        result.setCheckStatus("PASS");
    }
    
    /**
     * 一致性检查
     */
    private void executeConsistencyCheck(QualityRule rule, QualityCheckResult result) {
        // TODO: 实现一致性检查逻辑
        result.setCheckStatus("PASS");
    }
    
    /**
     * 唯一性检查
     */
    private void executeUniquenessCheck(QualityRule rule, QualityCheckResult result) {
        // TODO: 实现唯一性检查逻辑
        result.setCheckStatus("PASS");
    }
}