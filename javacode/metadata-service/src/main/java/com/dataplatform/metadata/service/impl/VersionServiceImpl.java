package com.dataplatform.metadata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dataplatform.metadata.dto.CreateVersionDTO;
import com.dataplatform.metadata.dto.VersionInfoDTO;
import com.dataplatform.metadata.entity.VersionInfo;
import com.dataplatform.metadata.entity.TableInfo;
import com.dataplatform.metadata.mapper.VersionInfoMapper;
import com.dataplatform.metadata.mapper.TableInfoMapper;
import com.dataplatform.metadata.service.VersionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VersionServiceImpl implements VersionService {
    
    @Autowired
    private VersionInfoMapper versionInfoMapper;
    
    @Autowired
    private TableInfoMapper tableInfoMapper;
    
    @Override
    @Transactional
    public VersionInfoDTO createVersion(CreateVersionDTO createDTO) {
        // 1. 获取当前表信息
        TableInfo tableInfo = tableInfoMapper.selectById(createDTO.getTableId());
        if (tableInfo == null) {
            throw new RuntimeException("表不存在");
        }
        
        // 2. 构造元数据内容
        JSONObject metadataContent = new JSONObject();
        metadataContent.put("tableInfo", tableInfo);
        
        // 3. 创建版本信息
        VersionInfo versionInfo = new VersionInfo();
        BeanUtils.copyProperties(createDTO, versionInfo);
        versionInfo.setMetadataContent(metadataContent.toJSONString());
        versionInfo.setCreatedBy("system"); // 实际项目中应从安全上下文中获取当前用户
        versionInfo.setCreatedTime(new Date());
        versionInfoMapper.insert(versionInfo);
        
        VersionInfoDTO result = new VersionInfoDTO();
        BeanUtils.copyProperties(versionInfo, result);
        return result;
    }
    
    @Override
    public List<VersionInfoDTO> getVersionsByTable(Long tableId) {
        List<VersionInfo> versionInfos = versionInfoMapper.selectByTableId(tableId);
        return versionInfos.stream()
                .map(versionInfo -> {
                    VersionInfoDTO dto = new VersionInfoDTO();
                    BeanUtils.copyProperties(versionInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}