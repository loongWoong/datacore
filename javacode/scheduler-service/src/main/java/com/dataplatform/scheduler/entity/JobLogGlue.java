package com.dataplatform.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("xxl_job_logglue")
@Schema(description = "任务GLUE日志表")
public class JobLogGlue {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @TableField("job_id")
    @Schema(description = "任务，主键ID")
    private Integer jobId;
    
    @TableField("glue_type")
    @Schema(description = "GLUE类型")
    private String glueType;
    
    @TableField("glue_source")
    @Schema(description = "GLUE源代码")
    private String glueSource;
    
    @TableField("glue_remark")
    @Schema(description = "GLUE备注")
    private String glueRemark;
    
    @TableField("add_time")
    @Schema(description = "添加时间")
    private Date addTime;
    
    @TableField("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;
}