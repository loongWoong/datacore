package com.dataplatform.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("xxl_job_log_report")
@Schema(description = "任务日志报告表")
public class JobLogReport {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @TableField("trigger_day")
    @Schema(description = "调度时间")
    private Date triggerDay;
    
    @TableField("running_count")
    @Schema(description = "运行中-日志数量")
    private Integer runningCount;
    
    @TableField("suc_count")
    @Schema(description = "执行成功-日志数量")
    private Integer sucCount;
    
    @TableField("fail_count")
    @Schema(description = "执行失败-日志数量")
    private Integer failCount;
    
    @TableField("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;
}