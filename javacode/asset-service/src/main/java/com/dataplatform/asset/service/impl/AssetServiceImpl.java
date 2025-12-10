package com.dataplatform.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dataplatform.asset.dto.*;
import com.dataplatform.asset.entity.*;
import com.dataplatform.asset.mapper.*;
import com.dataplatform.asset.service.AssetService;
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
public class AssetServiceImpl implements AssetService {
    
    @Autowired
    private AssetInfoMapper assetInfoMapper;
    
    @Autowired
    private AssetTagRelationMapper assetTagRelationMapper;
    
    @Autowired
    private AssetEvaluationMapper assetEvaluationMapper;
    
    @Autowired
    private AssetLifecycleMapper assetLifecycleMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    @Transactional
    public AssetInfoDTO createAsset(CreateAssetDTO createDTO) {
        // 1. 校验资产编码唯一性
        AssetInfo existingAsset = assetInfoMapper.selectByAssetCode(createDTO.getAssetCode());
        if (existingAsset != null) {
            throw new RuntimeException("资产编码已存在");
        }
        
        // 2. 创建资产信息
        AssetInfo assetInfo = new AssetInfo();
        BeanUtils.copyProperties(createDTO, assetInfo);
        assetInfo.setAssetStatus("ACTIVE");
        assetInfo.setCreatedTime(new Date());
        assetInfo.setUpdatedTime(new Date());
        assetInfoMapper.insert(assetInfo);
        
        // 3. 创建生命周期记录
        AssetLifecycle lifecycle = new AssetLifecycle();
        lifecycle.setAssetId(assetInfo.getId());
        lifecycle.setLifecycleEvent("CREATE");
        lifecycle.setEventDescription("资产创建");
        lifecycle.setOperator("system"); // 实际项目中应从安全上下文中获取当前用户
        lifecycle.setOperatedTime(new Date());
        assetLifecycleMapper.insert(lifecycle);
        
        // 4. 同步到ES（TODO: 实现ES同步逻辑）
        syncToElasticsearch(assetInfo);
        
        // 5. 清除缓存
        redisTemplate.delete("asset:list");
        
        // 6. 返回结果
        AssetInfoDTO result = new AssetInfoDTO();
        BeanUtils.copyProperties(assetInfo, result);
        return result;
    }
    
    @Override
    public PageResult<AssetInfoDTO> getAssets(AssetQueryDTO queryDTO) {
        // 1. 尝试从缓存获取
        String cacheKey = "asset:list:" + com.alibaba.fastjson.JSON.toJSONString(queryDTO);
        PageResult<AssetInfoDTO> cachedResult = (PageResult<AssetInfoDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 2. 从数据库查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AssetInfo> assets = assetInfoMapper.selectByCondition(queryDTO);
        
        List<AssetInfoDTO> dtos = assets.stream()
                .map(asset -> {
                    AssetInfoDTO dto = new AssetInfoDTO();
                    BeanUtils.copyProperties(asset, dto);
                    return dto;
                })
                .collect(Collectors.toList());
                
        PageInfo<AssetInfo> pageInfo = new PageInfo<>(assets);
        PageResult<AssetInfoDTO> result = PageResult.of(dtos, pageInfo.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 3. 存入缓存
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));
        
        return result;
    }
    
    @Override
    public AssetDetailDTO getAssetDetail(Long id) {
        // 1. 查询资产基本信息
        AssetInfo assetInfo = assetInfoMapper.selectById(id);
        if (assetInfo == null) {
            throw new RuntimeException("资产不存在");
        }
        
        // 2. 查询资产标签
        List<AssetTag> tags = assetTagRelationMapper.selectTagsByAssetId(id);
        
        // 3. 查询最新评估信息
        AssetEvaluation latestEvaluation = assetEvaluationMapper.selectLatestByAssetId(id);
        
        // 4. 查询生命周期记录
        List<AssetLifecycle> lifecycles = assetLifecycleMapper.selectByAssetId(id);
        
        // 5. 构造详细信息DTO
        AssetDetailDTO detailDTO = new AssetDetailDTO();
        BeanUtils.copyProperties(assetInfo, detailDTO);
        detailDTO.setTags(tags.stream().map(tag -> {
            AssetTagDTO tagDTO = new AssetTagDTO();
            BeanUtils.copyProperties(tag, tagDTO);
            return tagDTO;
        }).collect(Collectors.toList()));
        
        if (latestEvaluation != null) {
            AssetEvaluationDTO evaluationDTO = new AssetEvaluationDTO();
            BeanUtils.copyProperties(latestEvaluation, evaluationDTO);
            detailDTO.setLatestEvaluation(evaluationDTO);
        }
        
        detailDTO.setLifecycles(lifecycles.stream().map(lifecycle -> {
            AssetLifecycleDTO lifecycleDTO = new AssetLifecycleDTO();
            BeanUtils.copyProperties(lifecycle, lifecycleDTO);
            return lifecycleDTO;
        }).collect(Collectors.toList()));
        
        return detailDTO;
    }
    
    @Override
    @Transactional
    public AssetInfoDTO updateAsset(Long id, UpdateAssetDTO updateDTO) {
        AssetInfo existingAsset = assetInfoMapper.selectById(id);
        if (existingAsset == null) {
            throw new RuntimeException("资产不存在");
        }
        
        BeanUtils.copyProperties(updateDTO, existingAsset);
        existingAsset.setUpdatedTime(new Date());
        assetInfoMapper.updateById(existingAsset);
        
        // 创建生命周期记录
        AssetLifecycle lifecycle = new AssetLifecycle();
        lifecycle.setAssetId(id);
        lifecycle.setLifecycleEvent("UPDATE");
        lifecycle.setEventDescription("资产更新");
        lifecycle.setOperator("system"); // 实际项目中应从安全上下文中获取当前用户
        lifecycle.setOperatedTime(new Date());
        assetLifecycleMapper.insert(lifecycle);
        
        // 同步到ES（TODO: 实现ES同步逻辑）
        syncToElasticsearch(existingAsset);
        
        // 清除缓存
        redisTemplate.delete("asset:list");
        redisTemplate.delete("asset:detail:" + id);
        
        AssetInfoDTO result = new AssetInfoDTO();
        BeanUtils.copyProperties(existingAsset, result);
        return result;
    }
    
    /**
     * 同步资产信息到Elasticsearch
     */
    private void syncToElasticsearch(AssetInfo assetInfo) {
        try {
            // TODO: 实现同步到Elasticsearch的逻辑
            // 这里应该调用Elasticsearch客户端将资产信息同步到搜索引擎中
            // 示例代码：
            // AssetDocument document = new AssetDocument();
            // BeanUtils.copyProperties(assetInfo, document);
            // elasticsearchTemplate.save(document);
        } catch (Exception e) {
            // 记录日志，但不中断主流程
            System.err.println("同步资产到Elasticsearch失败: " + e.getMessage());
        }
    }
}