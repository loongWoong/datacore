package com.dataplatform.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.scheduler.entity.JobLogReport;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

public interface JobLogReportMapper extends BaseMapper<JobLogReport> {
    
    List<JobLogReport> selectRecentReports(@Param("days") int days);
    
    JobLogReport selectByTriggerDay(@Param("triggerDay") Date triggerDay);
}