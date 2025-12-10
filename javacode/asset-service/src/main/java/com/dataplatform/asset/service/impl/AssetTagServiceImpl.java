package com.dataplatform.asset.service.impl;

import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.entity.AssetTag;
import com.dataplatform.asset.entity.AssetTagRelation;
import com.dataplatform.asset.mapper.AssetTagMapper;
import com.dataplatform.asset.mapper.AssetTagRelationMapper;
import com.dataplatform.asset.service.AssetTagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetTagServiceImpl implements AssetTagService {
    
    @Autowired
    private AssetTagMapper assetTagMapper;
    
    @Autowired
    private AssetTagRelationMapper assetTagRelationMapper;
    
    @Override
    @Transactional
    public AssetTagDTO createTag(CreateTagDTO createDTO) {
        // 1. 创建标签
        AssetTag tag = new AssetTag();
        BeanUtils.copyProperties(createDTO, tag);
        tag.setCreator("system"); // 实际项目中应从安全上下文中获取当前用户
        tag.setCreatedTime(new Date());
        tag.setUpdatedTime(new Date());
        assetTagMapper.insert(tag);
        
        // 2. 返回结果
        AssetTagDTO result = new AssetTagDTO();
        BeanUtils.copyProperties(tag, result);
        return result;
    }
    
    @Override
    public List<AssetTagDTO> getTags(String tagCategory) {
        List<AssetTag> tags = assetTagMapper.selectByCategory(tagCategory);
        return tags.stream()
                .map(tag -> {
                    AssetTagDTO dto = new AssetTagDTO();
                    BeanUtils.copyProperties(tag, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void tagAsset(Long assetId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            // 检查关联是否已存在
            AssetTagRelation existingRelation = assetTagRelationMapper.selectByAssetIdAndTagId(assetId, tagId);
            if (existingRelation == null) {
                // 不存在则创建新的关联
                AssetTagRelation relation = new AssetTagRelation();
                relation.setAssetId(assetId);
                relation.setTagId(tagId);
                relation.setCreatedTime(new Date());
                assetTagRelationMapper.insert(relation);
            }
        }
    }
    
    @Override
    @Transactional
    public void untagAsset(Long assetId, Long tagId) {
        assetTagRelationMapper.deleteByAssetIdAndTagId(assetId, tagId);
    }
}