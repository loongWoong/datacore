package com.dataplatform.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("xxl_job_log")
@Schema(description = "调度日志表")
public class JobLog {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("job_group")
    @Schema(description = "执行器主键ID")
    private Integer jobGroup;
    
    @TableField("job_id")
    @Schema(description = "任务，主键ID")
    private Integer jobId;
    
    @TableField("executor_address")
    @Schema(description = "执行器地址，本次执行的地址")
    private String executorAddress;
    
    @TableField("executor_handler")
    @Schema(description = "执行器任务handler")
    private String executorHandler;
    
    @TableField("executor_param")
    @Schema(description = "执行器任务参数")
    private String executorParam;
    
    @TableField("executor_sharding_param")
    @Schema(description = "执行器任务分片参数，格式如 1/2")
    private String executorShardingParam;
    
    @TableField("executor_fail_retry_count")
    @Schema(description = "失败重试次数")
    private Integer executorFailRetryCount;
    
    @TableField("trigger_time")
    @Schema(description = "调度-时间")
    private Date triggerTime;
    
    @TableField("trigger_code")
    @Schema(description = "调度-结果")
    private Integer triggerCode;
    
    @TableField("trigger_msg")
    @Schema(description = "调度-日志")
    private String triggerMsg;
    
    @TableField("handle_time")
    @Schema(description = "执行-时间")
    private Date handleTime;
    
    @TableField("handle_code")
    @Schema(description = "执行-状态")
    private Integer handleCode;
    
    @TableField("handle_msg")
    @Schema(description = "执行-日志")
    private String handleMsg;
    
    @TableField("alarm_status")
    @Schema(description = "告警状态：0-默认，1-无需告警，2-告警成功，3-告警失败")
    private Integer alarmStatus;
}