package com.dataplatform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.report.entity.ReportSubscription;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ReportSubscriptionMapper extends BaseMapper<ReportSubscription> {
    
    List<ReportSubscription> selectByReportId(@Param("reportId") Long reportId);
    
    List<ReportSubscription> selectActiveSubscriptions(@Param("reportId") Long reportId);
}