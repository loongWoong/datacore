package com.dataplatform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.report.entity.ReportParameter;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ReportParameterMapper extends BaseMapper<ReportParameter> {
    
    List<ReportParameter> selectByReportId(@Param("reportId") Long reportId);
    
    void deleteByReportId(@Param("reportId") Long reportId);
}