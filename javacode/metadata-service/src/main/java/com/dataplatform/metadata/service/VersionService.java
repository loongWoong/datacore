package com.dataplatform.metadata.service;

import com.dataplatform.metadata.dto.CreateVersionDTO;
import com.dataplatform.metadata.dto.VersionInfoDTO;
import java.util.List;

public interface VersionService {
    
    /**
     * 创建版本元数据
     */
    VersionInfoDTO createVersion(CreateVersionDTO createDTO);
    
    /**
     * 获取表的版本列表
     */
    List<VersionInfoDTO> getVersionsByTable(Long tableId);
}