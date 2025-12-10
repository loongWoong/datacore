package com.dataplatform.metadata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataplatform.metadata.dto.*;
import com.dataplatform.metadata.entity.TableInfo;
import com.dataplatform.metadata.entity.ColumnInfo;
import com.dataplatform.metadata.mapper.TableInfoMapper;
import com.dataplatform.metadata.mapper.ColumnInfoMapper;
import com.dataplatform.metadata.service.TableService;
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
public class TableServiceImpl implements TableService {
    
    @Autowired
    private TableInfoMapper tableInfoMapper;
    
    @Autowired
    private ColumnInfoMapper columnInfoMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public TableInfoDTO createTable(CreateTableDTO createDTO) {
        // 1. 创建表信息
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(createDTO, tableInfo);
        tableInfo.setCreatedTime(new Date());
        tableInfo.setUpdatedTime(new Date());
        tableInfoMapper.insert(tableInfo);
        
        // 2. 创建字段信息
        if (createDTO.getColumns() != null) {
            for (CreateColumnDTO columnDTO : createDTO.getColumns()) {
                ColumnInfo columnInfo = new ColumnInfo();
                BeanUtils.copyProperties(columnDTO, columnInfo);
                columnInfo.setTableId(tableInfo.getId());
                columnInfo.setCreatedTime(new Date());
                columnInfo.setUpdatedTime(new Date());
                columnInfoMapper.insert(columnInfo);
            }
        }
        
        // 3. 清除缓存
        redisTemplate.delete("table:list");
        
        // 4. 返回结果
        TableInfoDTO result = new TableInfoDTO();
        BeanUtils.copyProperties(tableInfo, result);
        return result;
    }
    
    @Override
    public List<TableInfoDTO> getTables(TableQueryDTO queryDTO) {
        // 1. 尝试从缓存获取
        String cacheKey = "table:list:" + com.alibaba.fastjson.JSON.toJSONString(queryDTO);
        List<TableInfoDTO> cachedResult = (List<TableInfoDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        List<TableInfo> tableInfos = tableInfoMapper.selectByCondition(queryDTO);
        List<TableInfoDTO> result = tableInfos.stream()
                .map(tableInfo -> {
                    TableInfoDTO dto = new TableInfoDTO();
                    BeanUtils.copyProperties(tableInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
    
    @Override
    public TableDetailDTO getTableDetail(Long id) {
        // 1. 查询表信息
        TableInfo tableInfo = tableInfoMapper.selectById(id);
        if (tableInfo == null) {
            return null;
        }
        
        // 2. 查询字段信息
        List<ColumnInfo> columnInfos = columnInfoMapper.selectByTableId(id);
        List<ColumnInfoDTO> columnDTOs = columnInfos.stream()
                .map(columnInfo -> {
                    ColumnInfoDTO dto = new ColumnInfoDTO();
                    BeanUtils.copyProperties(columnInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 3. 组装返回结果
        TableDetailDTO result = new TableDetailDTO();
        BeanUtils.copyProperties(tableInfo, result);
        result.setColumns(columnDTOs);
        
        return result;
    }
}