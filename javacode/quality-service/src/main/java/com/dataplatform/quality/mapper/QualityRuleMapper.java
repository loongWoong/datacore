package com.dataplatform.quality.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.quality.entity.QualityRule;
import com.dataplatform.quality.dto.QualityRuleQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface QualityRuleMapper extends BaseMapper<QualityRule> {
    
    QualityRule selectByRuleCode(@Param("ruleCode") String ruleCode);
    
    List<QualityRule> selectByCondition(@Param("query") QualityRuleQuery query);
    
    List<QualityRule> selectBatchIds(@Param("ids") List<Long> ids);
}