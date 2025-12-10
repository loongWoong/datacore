package com.dataplatform.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("xxl_job_info")
@Schema(description = "任务信息表")
public class JobInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    
    @TableField("job_group")
    @Schema(description = "执行器主键ID")
    private Integer jobGroup;
    
    @TableField("job_desc")
    @Schema(description = "任务描述")
    private String jobDesc;
    
    @TableField("add_time")
    @Schema(description = "添加时间")
    private Date addTime;
    
    @TableField("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;
    
    @TableField("author")
    @Schema(description = "作者")
    private String author;
    
    @TableField("alarm_email")
    @Schema(description = "报警邮件")
    private String alarmEmail;
    
    @TableField("schedule_type")
    @Schema(description = "调度类型")
    private String scheduleType;
    
    @TableField("schedule_conf")
    @Schema(description = "调度配置，值含义取决于调度类型")
    private String scheduleConf;
    
    @TableField("misfire_strategy")
    @Schema(description = "调度过期策略")
    private String misfireStrategy;
    
    @TableField("executor_route_strategy")
    @Schema(description = "执行器路由策略")
    private String executorRouteStrategy;
    
    @TableField("executor_handler")
    @Schema(description = "执行器任务handler")
    private String executorHandler;
    
    @TableField("executor_param")
    @Schema(description = "执行器任务参数")
    private String executorParam;
    
    @TableField("executor_block_strategy")
    @Schema(description = "阻塞处理策略")
    private String executorBlockStrategy;
    
    @TableField("executor_timeout")
    @Schema(description = "任务执行超时时间，单位秒")
    private Integer executorTimeout;
    
    @TableField("executor_fail_retry_count")
    @Schema(description = "失败重试次数")
    private Integer executorFailRetryCount;
    
    @TableField("glue_type")
    @Schema(description = "GLUE类型")
    private String glueType;
    
    @TableField("glue_source")
    @Schema(description = "GLUE源代码")
    private String glueSource;
    
    @TableField("glue_remark")
    @Schema(description = "GLUE备注")
    private String glueRemark;
    
    @TableField("glue_updatetime")
    @Schema(description = "GLUE更新时间")
    private Date glueUpdatetime;
    
    @TableField("child_jobid")
    @Schema(description = "子任务ID，多个逗号分隔")
    private String childJobid;
    
    @TableField("trigger_status")
    @Schema(description = "调度状态：0-停止，1-运行")
    private Integer triggerStatus;
    
    @TableField("trigger_last_time")
    @Schema(description = "上次调度时间")
    private Long triggerLastTime;
    
    @TableField("trigger_next_time")
    @Schema(description = "下次调度时间")
    private Long triggerNextTime;
}