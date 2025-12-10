package com.dataplatform.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.scheduler.entity.JobInfo;
import com.dataplatform.scheduler.dto.JobInfoQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JobInfoMapper extends BaseMapper<JobInfo> {
    
    List<JobInfo> selectByCondition(@Param("query") JobInfoQuery query);
}