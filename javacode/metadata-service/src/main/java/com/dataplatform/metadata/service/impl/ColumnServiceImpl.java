package com.dataplatform.metadata.service.impl;

import com.dataplatform.metadata.dto.CreateColumnDTO;
import com.dataplatform.metadata.dto.ColumnInfoDTO;
import com.dataplatform.metadata.entity.ColumnInfo;
import com.dataplatform.metadata.mapper.ColumnInfoMapper;
import com.dataplatform.metadata.service.ColumnService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnServiceImpl implements ColumnService {
    
    @Autowired
    private ColumnInfoMapper columnInfoMapper;
    
    @Override
    @Transactional
    public ColumnInfoDTO createColumn(CreateColumnDTO createDTO) {
        ColumnInfo columnInfo = new ColumnInfo();
        BeanUtils.copyProperties(createDTO, columnInfo);
        columnInfo.setCreatedTime(new Date());
        columnInfo.setUpdatedTime(new Date());
        columnInfoMapper.insert(columnInfo);
        
        ColumnInfoDTO result = new ColumnInfoDTO();
        BeanUtils.copyProperties(columnInfo, result);
        return result;
    }
    
    @Override
    public List<ColumnInfoDTO> getColumnsByTable(Long tableId) {
        List<ColumnInfo> columnInfos = columnInfoMapper.selectByTableId(tableId);
        return columnInfos.stream()
                .map(columnInfo -> {
                    ColumnInfoDTO dto = new ColumnInfoDTO();
                    BeanUtils.copyProperties(columnInfo, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}