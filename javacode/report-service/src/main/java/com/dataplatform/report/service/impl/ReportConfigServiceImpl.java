package com.dataplatform.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataplatform.report.dto.*;
import com.dataplatform.report.entity.ReportConfig;
import com.dataplatform.report.entity.ReportParameter;
import com.dataplatform.report.mapper.ReportConfigMapper;
import com.dataplatform.report.mapper.ReportParameterMapper;
import com.dataplatform.report.service.ReportConfigService;
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
public class ReportConfigServiceImpl implements ReportConfigService {
    
    @Autowired
    private ReportConfigMapper reportConfigMapper;
    
    @Autowired
    private ReportParameterMapper reportParameterMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public ReportConfigDTO createReportConfig(CreateReportConfigDTO createDTO) {
        // 1. 校验报表编码唯一性
        ReportConfig existingConfig = reportConfigMapper.selectByReportCode(createDTO.getReportCode());
        if (existingConfig != null) {
            throw new RuntimeException("报表编码已存在");
        }
        
        // 2. 创建报表配置
        ReportConfig config = new ReportConfig();
        BeanUtils.copyProperties(createDTO, config);
        config.setCreator("system"); // 实际项目中应从安全上下文中获取当前用户
        config.setCreatedTime(new Date());
        config.setUpdatedTime(new Date());
        config.setIsEnabled(1);
        reportConfigMapper.insert(config);
        
        // 3. 创建报表参数
        if (createDTO.getParameters() != null) {
            for (CreateReportParameterDTO paramDTO : createDTO.getParameters()) {
                ReportParameter parameter = new ReportParameter();
                BeanUtils.copyProperties(paramDTO, parameter);
                parameter.setReportId(config.getId());
                parameter.setCreatedTime(new Date());
                reportParameterMapper.insert(parameter);
            }
        }
        
        // 4. 清除缓存
        redisTemplate.delete("report:configs");
        
        // 5. 返回结果
        ReportConfigDTO result = new ReportConfigDTO();
        BeanUtils.copyProperties(config, result);
        return result;
    }
    
    @Override
    public PageResult<ReportConfigDTO> getReportConfigs(ReportConfigQuery query) {
        // 1. 尝试从缓存获取
        String cacheKey = "report:configs:" + com.alibaba.fastjson.JSON.toJSONString(query);
        PageResult<ReportConfigDTO> cachedResult = (PageResult<ReportConfigDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ReportConfig> configs = reportConfigMapper.selectByCondition(query);
        
        List<ReportConfigDTO> dtos = configs.stream()
                .map(config -> {
                    ReportConfigDTO dto = new ReportConfigDTO();
                    BeanUtils.copyProperties(config, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<ReportConfig> pageInfo = new PageInfo<>(configs);
        PageResult<ReportConfigDTO> result = PageResult.of(dtos, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
    
    @Override
    public ReportConfigDetailDTO getReportConfigDetail(Long id) {
        // 1. 查询报表配置
        ReportConfig config = reportConfigMapper.selectById(id);
        if (config == null) {
            throw new RuntimeException("报表配置不存在");
        }
        
        // 2. 查询报表参数
        List<ReportParameter> parameters = reportParameterMapper.selectByReportId(id);
        
        // 3. 构造详细信息DTO
        ReportConfigDetailDTO detailDTO = new ReportConfigDetailDTO();
        BeanUtils.copyProperties(config, detailDTO);
        
        List<ReportParameterDTO> parameterDTOs = parameters.stream()
                .map(param -> {
                    ReportParameterDTO paramDTO = new ReportParameterDTO();
                    BeanUtils.copyProperties(param, paramDTO);
                    return paramDTO;
                })
                .collect(Collectors.toList());
                
        detailDTO.setParameters(parameterDTOs);
        
        return detailDTO;
    }
    
    @Override
    @Transactional
    public ReportConfigDTO updateReportConfig(Long id, UpdateReportConfigDTO updateDTO) {
        ReportConfig existingConfig = reportConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new RuntimeException("报表配置不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingConfig);
        existingConfig.setUpdatedTime(new Date());
        reportConfigMapper.updateById(existingConfig);
        
        // 更新参数
        if (updateDTO.getParameters() != null) {
            // 先删除原有参数
            reportParameterMapper.deleteByReportId(id);
            
            // 再插入新参数
            for (UpdateReportParameterDTO paramDTO : updateDTO.getParameters()) {
                ReportParameter parameter = new ReportParameter();
                BeanUtils.copyProperties(paramDTO, parameter);
                parameter.setReportId(id);
                parameter.setCreatedTime(new Date());
                reportParameterMapper.insert(parameter);
            }
        }
        
        // 清除缓存
        redisTemplate.delete("report:configs");
        redisTemplate.delete("report:config:" + id);
        
        ReportConfigDTO result = new ReportConfigDTO();
        BeanUtils.copyProperties(existingConfig, result);
        return result;
    }
    
    @Override
    @Transactional
    public void deleteReportConfig(Long id) {
        ReportConfig existingConfig = reportConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new RuntimeException("报表配置不存在");
        }
        
        // 删除相关参数
        reportParameterMapper.deleteByReportId(id);
        
        // 删除配置
        reportConfigMapper.deleteById(id);
        
        // 清除缓存
        redisTemplate.delete("report:configs");
        redisTemplate.delete("report:config:" + id);
    }
}