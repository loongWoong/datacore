package com.dataplatform.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.scheduler.entity.JobRegistry;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

public interface JobRegistryMapper extends BaseMapper<JobRegistry> {
    
    List<JobRegistry> selectByRegistryGroupAndKey(@Param("registryGroup") String registryGroup, 
                                                 @Param("registryKey") String registryKey);
    
    void deleteByUpdateTimeBefore(@Param("beforeTime") Date beforeTime);
}