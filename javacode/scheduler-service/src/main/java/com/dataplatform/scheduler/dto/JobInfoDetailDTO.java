package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "任务信息详情DTO")
public class JobInfoDetailDTO {
    
    @Schema(description = "主键ID")
    private Integer id;
    
    @Schema(description = "执行器主键ID")
    private Integer jobGroup;
    
    @Schema(description = "任务描述")
    private String jobDesc;
    
    @Schema(description = "添加时间")
    private Date addTime;
    
    @Schema(description = "更新时间")
    private Date updateTime;
    
    @Schema(description = "作者")
    private String author;
    
    @Schema(description = "报警邮件")
    private String alarmEmail;
    
    @Schema(description = "调度类型")
    private String scheduleType;
    
    @Schema(description = "调度配置，值含义取决于调度类型")
    private String scheduleConf;
    
    @Schema(description = "调度过期策略")
    private String misfireStrategy;
    
    @Schema(description = "执行器路由策略")
    private String executorRouteStrategy;
    
    @Schema(description = "执行器任务handler")
    private String executorHandler;
    
    @Schema(description = "执行器任务参数")
    private String executorParam;
    
    @Schema(description = "阻塞处理策略")
    private String executorBlockStrategy;
    
    @Schema(description = "任务执行超时时间，单位秒")
    private Integer executorTimeout;
    
    @Schema(description = "失败重试次数")
    private Integer executorFailRetryCount;
    
    @Schema(description = "GLUE类型")
    private String glueType;
    
    @Schema(description = "GLUE备注")
    private String glueRemark;
    
    @Schema(description = "子任务ID，多个逗号分隔")
    private String childJobid;
    
    @Schema(description = "调度状态：0-停止，1-运行")
    private Integer triggerStatus;
    
    @Schema(description = "上次调度时间")
    private Long triggerLastTime;
    
    @Schema(description = "下次调度时间")
    private Long triggerNextTime;
}