package com.dataplatform.asset.service.impl;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.entity.AssetInfo;
import com.dataplatform.asset.mapper.AssetInfoMapper;
import com.dataplatform.asset.service.AssetSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetSearchServiceImpl implements AssetSearchService {
    
    @Autowired
    private AssetInfoMapper assetInfoMapper;
    
    @Override
    public PageResult<AssetInfoDTO> searchAssets(String keyword, Integer pageNum, Integer pageSize) {
        // 使用PageHelper进行分页
        PageHelper.startPage(pageNum, pageSize);
        
        // 执行搜索查询
        List<AssetInfo> assets = assetInfoMapper.selectListByKeyword(keyword);
        
        // 转换为DTO
        List<AssetInfoDTO> dtos = assets.stream()
                .map(asset -> {
                    AssetInfoDTO dto = new AssetInfoDTO();
                    org.springframework.beans.BeanUtils.copyProperties(asset, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 获取分页信息
        PageInfo<AssetInfo> pageInfo = new PageInfo<>(assets);
        
        // 构造返回结果
        return PageResult.of(dtos, pageInfo.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public PageResult<AssetInfoDTO> advancedSearch(AdvancedSearchDTO searchDTO) {
        // 使用PageHelper进行分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        
        // 执行高级搜索查询
        List<AssetInfo> assets = assetInfoMapper.selectByAdvancedCondition(searchDTO);
        
        // 转换为DTO
        List<AssetInfoDTO> dtos = assets.stream()
                .map(asset -> {
                    AssetInfoDTO dto = new AssetInfoDTO();
                    org.springframework.beans.BeanUtils.copyProperties(asset, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 获取分页信息
        PageInfo<AssetInfo> pageInfo = new PageInfo<>(assets);
        
        // 构造返回结果
        return PageResult.of(dtos, pageInfo.getTotal(), searchDTO.getPageNum(), searchDTO.getPageSize());
    }
}