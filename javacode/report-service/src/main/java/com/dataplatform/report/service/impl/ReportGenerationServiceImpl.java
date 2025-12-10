package com.dataplatform.report.service.impl;

import com.dataplatform.report.dto.*;
import com.dataplatform.report.entity.ReportConfig;
import com.dataplatform.report.entity.ReportGenerationRecord;
import com.dataplatform.report.entity.ReportParameter;
import com.dataplatform.report.mapper.ReportConfigMapper;
import com.dataplatform.report.mapper.ReportGenerationRecordMapper;
import com.dataplatform.report.mapper.ReportParameterMapper;
import com.dataplatform.report.service.ReportGenerationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {
    
    @Autowired
    private ReportConfigMapper reportConfigMapper;
    
    @Autowired
    private ReportParameterMapper reportParameterMapper;
    
    @Autowired
    private ReportGenerationRecordMapper reportGenerationRecordMapper;
    
    @Override
    // @Async // 实际项目中应启用异步执行
    public ReportGenerationResultDTO generateReport(GenerateReportDTO generateDTO) {
        // 1. 查询报表配置
        ReportConfig config = reportConfigMapper.selectById(generateDTO.getReportId());
        if (config == null) {
            throw new RuntimeException("报表配置不存在");
        }
        
        // 2. 创建生成记录
        ReportGenerationRecord record = new ReportGenerationRecord();
        record.setReportId(config.getId());
        record.setReportCode(config.getReportCode());
        record.setReportName(config.getReportName());
        record.setGenerationStatus("PENDING");
        record.setGeneratedBy("system"); // 实际项目中应从安全上下文中获取当前用户
        record.setGeneratedParams(com.alibaba.fastjson.JSON.toJSONString(generateDTO.getParameters()));
        record.setCreatedTime(new Date());
        reportGenerationRecordMapper.insert(record);
        
        try {
            // 3. 更新状态为RUNNING
            record.setGenerationStatus("RUNNING");
            record.setStartTime(new Date());
            reportGenerationRecordMapper.updateById(record);
            
            // 4. 参数校验和处理
            validateAndProcessParameters(config.getId(), generateDTO.getParameters());
            
            // 5. 生成报表（模拟实现）
            String fileName = config.getReportName() + "_" + System.currentTimeMillis() + getFileExtension(config.getOutputFormat());
            long fileSize = 102400; // 模拟文件大小
            
            // 6. 更新生成记录
            record.setGenerationStatus("SUCCESS");
            record.setEndTime(new Date());
            record.setExecutionTime(System.currentTimeMillis() - record.getStartTime().getTime());
            record.setFilePath("/reports/" + fileName);
            record.setFileSize(fileSize);
            reportGenerationRecordMapper.updateById(record);
            
            // 7. 返回结果
            ReportGenerationResultDTO result = new ReportGenerationResultDTO();
            result.setSuccess(true);
            result.setRecordId(record.getId());
            result.setFileName(fileName);
            result.setFileSize(fileSize);
            return result;
            
        } catch (Exception e) {
            // 更新生成记录为失败状态
            record.setGenerationStatus("FAILED");
            record.setEndTime(new Date());
            record.setErrorMessage(e.getMessage());
            reportGenerationRecordMapper.updateById(record);
            
            throw new RuntimeException("报表生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public PageResult<ReportGenerationRecordDTO> getGenerationRecords(GenerationRecordQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<ReportGenerationRecord> records = reportGenerationRecordMapper.selectByCondition(query);
        
        List<ReportGenerationRecordDTO> dtos = records.stream()
                .map(record -> {
                    ReportGenerationRecordDTO dto = new ReportGenerationRecordDTO();
                    BeanUtils.copyProperties(record, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<ReportGenerationRecord> pageInfo = new PageInfo<>(records);
        return PageResult.of(dtos, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }
    
    @Override
    public Resource downloadReport(Long recordId) {
        // 实际项目中应实现文件下载逻辑
        throw new UnsupportedOperationException("文件下载功能需要根据具体的文件存储方案实现");
    }
    
    /**
     * 参数校验和处理
     */
    private void validateAndProcessParameters(Long reportId, Map<String, Object> parameters) {
        List<ReportParameter> reportParameters = reportParameterMapper.selectByReportId(reportId);
        
        for (ReportParameter param : reportParameters) {
            Object value = parameters.get(param.getParamName());
            
            // 必填参数校验
            if (param.getIsRequired() != null && param.getIsRequired() == 1 && (value == null || value.toString().isEmpty())) {
                throw new RuntimeException("参数 " + param.getParamLabel() + " 不能为空");
            }
            
            // 默认值处理
            if (value == null && param.getDefaultValue() != null && !param.getDefaultValue().isEmpty()) {
                parameters.put(param.getParamName(), param.getDefaultValue());
            }
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String outputFormat) {
        if (outputFormat == null) {
            return ".xlsx";
        }
        
        switch (outputFormat.toUpperCase()) {
            case "PDF":
                return ".pdf";
            case "HTML":
                return ".html";
            case "CSV":
                return ".csv";
            default:
                return ".xlsx";
        }
    }
}