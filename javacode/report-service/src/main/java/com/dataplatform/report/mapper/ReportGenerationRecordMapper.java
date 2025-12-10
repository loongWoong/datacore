package com.dataplatform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.report.entity.ReportGenerationRecord;
import com.dataplatform.report.dto.GenerationRecordQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ReportGenerationRecordMapper extends BaseMapper<ReportGenerationRecord> {
    
    List<ReportGenerationRecord> selectByCondition(@Param("query") GenerationRecordQuery query);
}