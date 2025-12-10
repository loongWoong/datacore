package com.dataplatform.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.scheduler.entity.JobLogGlue;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface JobLogGlueMapper extends BaseMapper<JobLogGlue> {
    
    List<JobLogGlue> selectByJobId(@Param("jobId") Integer jobId);
    
    void deleteByJobId(@Param("jobId") Integer jobId);
}