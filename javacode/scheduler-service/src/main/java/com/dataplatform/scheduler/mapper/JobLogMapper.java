package com.dataplatform.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.scheduler.entity.JobLog;
import com.dataplatform.scheduler.dto.JobLogQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JobLogMapper extends BaseMapper<JobLog> {
    
    List<JobLog> selectByCondition(@Param("query") JobLogQuery query);
}