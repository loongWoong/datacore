package com.dataplatform.datasource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataplatform.datasource.dto.CreateDatasourceDTO;
import com.dataplatform.datasource.dto.DatasourceInfoDTO;
import com.dataplatform.datasource.dto.DatasourceQueryDTO;
import com.dataplatform.datasource.dto.UpdateDatasourceDTO;
import com.dataplatform.datasource.entity.DatasourceInfo;
import com.dataplatform.datasource.mapper.DatasourceInfoMapper;
import com.dataplatform.datasource.service.DatasourceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatasourceServiceImpl extends ServiceImpl<DatasourceInfoMapper, DatasourceInfo> implements DatasourceService {
    
    @Override
    public List<DatasourceInfoDTO> getDatasources(DatasourceQueryDTO queryDTO) {
        QueryWrapper<DatasourceInfo> queryWrapper = new QueryWrapper<>();
        
        if (queryDTO != null) {
            if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
                queryWrapper.like("name", queryDTO.getName());
            }
            if (queryDTO.getType() != null && !queryDTO.getType().isEmpty()) {
                queryWrapper.eq("type", queryDTO.getType());
            }
            if (queryDTO.getOwner() != null && !queryDTO.getOwner().isEmpty()) {
                queryWrapper.eq("owner", queryDTO.getOwner());
            }
            if (queryDTO.getEnvironment() != null && !queryDTO.getEnvironment().isEmpty()) {
                queryWrapper.eq("environment", queryDTO.getEnvironment());
            }
            if (queryDTO.getStatus() != null && !queryDTO.getStatus().isEmpty()) {
                queryWrapper.eq("status", queryDTO.getStatus());
            }
            if (queryDTO.getSearch() != null && !queryDTO.getSearch().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like("name", queryDTO.getSearch())
                    .or()
                    .like("description", queryDTO.getSearch())
                    .or()
                    .like("owner", queryDTO.getSearch()));
            }
        }
        
        queryWrapper.orderByDesc("created_time");
        List<DatasourceInfo> datasourceList = this.list(queryWrapper);
        
        return datasourceList.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public IPage<DatasourceInfoDTO> getDatasourcePage(int pageNum, int pageSize, DatasourceQueryDTO queryDTO) {
        Page<DatasourceInfo> page = new Page<>(pageNum, pageSize);
        
        DatasourceInfo queryEntity = new DatasourceInfo();
        if (queryDTO != null) {
            BeanUtils.copyProperties(queryDTO, queryEntity);
        }
        
        IPage<DatasourceInfo> datasourcePage = this.baseMapper.selectDatasourcePage(page, queryEntity);
        
        Page<DatasourceInfoDTO> dtoPage = new Page<>(pageNum, pageSize, datasourcePage.getTotal());
        List<DatasourceInfoDTO> dtoList = datasourcePage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }
    
    @Override
    public DatasourceInfoDTO getDatasourceDetail(Long id) {
        DatasourceInfo datasourceInfo = this.getById(id);
        if (datasourceInfo == null) {
            return null;
        }
        return convertToDTO(datasourceInfo);
    }
    
    @Override
    public DatasourceInfoDTO createDatasource(CreateDatasourceDTO createDTO) {
        DatasourceInfo datasourceInfo = new DatasourceInfo();
        BeanUtils.copyProperties(createDTO, datasourceInfo);
        
        // 设置默认值
        datasourceInfo.setStatus("ACTIVE");
        datasourceInfo.setConnectionUrl(buildConnectionUrl(createDTO));
        datasourceInfo.setDriverClass(getDriverClass(createDTO.getType()));
        
        this.save(datasourceInfo);
        return convertToDTO(datasourceInfo);
    }
    
    @Override
    public DatasourceInfoDTO updateDatasource(Long id, UpdateDatasourceDTO updateDTO) {
        DatasourceInfo datasourceInfo = this.getById(id);
        if (datasourceInfo == null) {
            return null;
        }
        
        BeanUtils.copyProperties(updateDTO, datasourceInfo);
        
        // 如果更新了连接信息，重新构建连接URL
        if (updateDTO.getHost() != null || updateDTO.getPort() != null || 
            updateDTO.getDatabaseName() != null) {
            datasourceInfo.setConnectionUrl(buildConnectionUrlFromEntity(datasourceInfo));
        }
        
        this.updateById(datasourceInfo);
        return convertToDTO(datasourceInfo);
    }
    
    @Override
    public boolean deleteDatasource(Long id) {
        return this.removeById(id);
    }
    
    @Override
    public boolean testConnection(Long id) {
        // 这里应该实现真实的连接测试逻辑
        // 由于这是一个示例，我们直接返回true
        return true;
    }
    
    private DatasourceInfoDTO convertToDTO(DatasourceInfo datasourceInfo) {
        DatasourceInfoDTO dto = new DatasourceInfoDTO();
        BeanUtils.copyProperties(datasourceInfo, dto);
        return dto;
    }
    
    private String buildConnectionUrl(CreateDatasourceDTO createDTO) {
        switch (createDTO.getType().toUpperCase()) {
            case "MYSQL":
                return String.format("jdbc:mysql://%s:%d/%s", 
                    createDTO.getHost(), createDTO.getPort(), createDTO.getDatabaseName());
            case "POSTGRESQL":
                return String.format("jdbc:postgresql://%s:%d/%s", 
                    createDTO.getHost(), createDTO.getPort(), createDTO.getDatabaseName());
            case "ORACLE":
                return String.format("jdbc:oracle:thin:@%s:%d:%s", 
                    createDTO.getHost(), createDTO.getPort(), createDTO.getDatabaseName());
            default:
                return String.format("jdbc:%s://%s:%d/%s", 
                    createDTO.getType().toLowerCase(), createDTO.getHost(), createDTO.getPort(), createDTO.getDatabaseName());
        }
    }
    
    private String buildConnectionUrlFromEntity(DatasourceInfo datasourceInfo) {
        switch (datasourceInfo.getType().toUpperCase()) {
            case "MYSQL":
                return String.format("jdbc:mysql://%s:%d/%s", 
                    datasourceInfo.getHost(), datasourceInfo.getPort(), datasourceInfo.getDatabaseName());
            case "POSTGRESQL":
                return String.format("jdbc:postgresql://%s:%d/%s", 
                    datasourceInfo.getHost(), datasourceInfo.getPort(), datasourceInfo.getDatabaseName());
            case "ORACLE":
                return String.format("jdbc:oracle:thin:@%s:%d:%s", 
                    datasourceInfo.getHost(), datasourceInfo.getPort(), datasourceInfo.getDatabaseName());
            default:
                return String.format("jdbc:%s://%s:%d/%s", 
                    datasourceInfo.getType().toLowerCase(), datasourceInfo.getHost(), datasourceInfo.getPort(), datasourceInfo.getDatabaseName());
        }
    }
    
    private String getDriverClass(String type) {
        switch (type.toUpperCase()) {
            case "MYSQL":
                return "com.mysql.cj.jdbc.Driver";
            case "POSTGRESQL":
                return "org.postgresql.Driver";
            case "ORACLE":
                return "oracle.jdbc.driver.OracleDriver";
            default:
                return "";
        }
    }
}