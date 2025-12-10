package com.dataplatform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.report.entity.ReportConfig;
import com.dataplatform.report.dto.ReportConfigQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ReportConfigMapper extends BaseMapper<ReportConfig> {
    
    ReportConfig selectByReportCode(@Param("reportCode") String reportCode);
    
    List<ReportConfig> selectByCondition(@Param("query") ReportConfigQuery query);
}