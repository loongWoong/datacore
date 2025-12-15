package com.dataplatform.scheduler.service.impl;

import com.alibaba.fastjson.JSON;
import com.dataplatform.scheduler.dto.*;
import com.dataplatform.scheduler.entity.JobInfo;
import com.dataplatform.scheduler.mapper.JobInfoMapper;
import com.dataplatform.scheduler.service.JobInfoService;
import com.dataplatform.scheduler.service.MysqlMetadataScanService;
import com.dataplatform.scheduler.service.DatasourceService;
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
    
    @Autowired
    private MysqlMetadataScanService mysqlMetadataScanService;
    
    @Autowired
    private DatasourceService datasourceService;
    
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
        String cacheKey = "scheduler:jobs:" + JSON.toJSONString(query);
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
        
        // 根据任务类型执行相应的任务
        if ("mysql_metadata_scanner".equals(existingJob.getExecutorHandler())) {
            executeMysqlScanTask(existingJob);
        } else if ("dbt_model_generator".equals(existingJob.getExecutorHandler())) {
            executeDbtModelGenerateTask(existingJob);
        } else if ("metadata_uploader".equals(existingJob.getExecutorHandler())) {
            executeMetadataUploadTask(existingJob);
        }
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
    
    /**
     * 执行MySQL元数据扫描任务
     * 
     * @param job 任务信息
     */
    private void executeMysqlScanTask(JobInfo job) {
        try {
            // 解析参数
            MysqlMetadataScanService.MysqlScanTaskDTO taskDTO = new MysqlMetadataScanService.MysqlScanTaskDTO();
            if (job.getExecutorParam() != null && !job.getExecutorParam().isEmpty()) {
                taskDTO = JSON.parseObject(job.getExecutorParam(), MysqlMetadataScanService.MysqlScanTaskDTO.class);
            }
            
            // 执行扫描任务
            String result = mysqlMetadataScanService.executeScan(taskDTO);
            System.out.println("MySQL扫描任务执行结果: " + result);
        } catch (Exception e) {
            System.err.println("执行MySQL扫描任务时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行DBT模型生成任务
     * 
     * @param job 任务信息
     */
    private void executeDbtModelGenerateTask(JobInfo job) {
        try {
            // 解析参数
            DbtModelGenerateTaskDTO taskDTO = new DbtModelGenerateTaskDTO();
            if (job.getExecutorParam() != null && !job.getExecutorParam().isEmpty()) {
                taskDTO = JSON.parseObject(job.getExecutorParam(), DbtModelGenerateTaskDTO.class);
            }
            
            // 执行模型生成任务
            String result = executePythonScript("meta-dbt/generate_dbt_models.py", taskDTO);
            System.out.println("DBT模型生成任务执行结果: " + result);
        } catch (Exception e) {
            System.err.println("执行DBT模型生成任务时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行元数据上传任务
     * 
     * @param job 任务信息
     */
    private void executeMetadataUploadTask(JobInfo job) {
        try {
            // 解析参数
            MetadataUploadTaskDTO taskDTO = new MetadataUploadTaskDTO();
            if (job.getExecutorParam() != null && !job.getExecutorParam().isEmpty()) {
                taskDTO = JSON.parseObject(job.getExecutorParam(), MetadataUploadTaskDTO.class);
            }
            
            // 执行元数据上传任务
            String result = executePythonScript("meta-dbt/collect_and_upload.py", taskDTO);
            System.out.println("元数据上传任务执行结果: " + result);
        } catch (Exception e) {
            System.err.println("执行元数据上传任务时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行Python脚本
     * 
     * @param scriptPath 脚本路径
     * @param taskDTO 任务参数
     * @return 执行结果
     */
    private String executePythonScript(String scriptPath, Object taskDTO) {
        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("python", scriptPath);
            
            // 设置工作目录
            java.nio.file.Path workingDir = java.nio.file.Paths.get(System.getProperty("user.dir")).getParent();
            processBuilder.directory(workingDir.toFile());
            
            // 启动进程
            Process process = processBuilder.start();
            
            // 等待进程完成
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                return scriptPath + " 脚本执行成功";
            } else {
                return scriptPath + " 脚本执行失败，退出码: " + exitCode;
            }
        } catch (Exception e) {
            return "执行 " + scriptPath + " 脚本时发生错误: " + e.getMessage();
        }
    }
    
    // DBT模型生成任务DTO
    public static class DbtModelGenerateTaskDTO {
        private String inputPath = "metadata_scan_result.yaml";
        private String modelsDir = "models";
        private Integer executorTimeout = 300;
        private Integer executorFailRetryCount = 0;
        
        // Getters and setters
        public String getInputPath() { return inputPath; }
        public void setInputPath(String inputPath) { this.inputPath = inputPath; }
        
        public String getModelsDir() { return modelsDir; }
        public void setModelsDir(String modelsDir) { this.modelsDir = modelsDir; }
        
        public Integer getExecutorTimeout() { return executorTimeout; }
        public void setExecutorTimeout(Integer executorTimeout) { this.executorTimeout = executorTimeout; }
        
        public Integer getExecutorFailRetryCount() { return executorFailRetryCount; }
        public void setExecutorFailRetryCount(Integer executorFailRetryCount) { this.executorFailRetryCount = executorFailRetryCount; }
    }
    
    // 元数据上传任务DTO
    public static class MetadataUploadTaskDTO {
        private String metadataFile = "metadata_scan_result.yaml";
        private Integer executorTimeout = 300;
        private Integer executorFailRetryCount = 0;
        
        // Getters and setters
        public String getMetadataFile() { return metadataFile; }
        public void setMetadataFile(String metadataFile) { this.metadataFile = metadataFile; }
        
        public Integer getExecutorTimeout() { return executorTimeout; }
        public void setExecutorTimeout(Integer executorTimeout) { this.executorTimeout = executorTimeout; }
        
        public Integer getExecutorFailRetryCount() { return executorFailRetryCount; }
        public void setExecutorFailRetryCount(Integer executorFailRetryCount) { this.executorFailRetryCount = executorFailRetryCount; }
    }
}