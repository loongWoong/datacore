package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@Schema(description = "任务日志DTO")
public class JobLogDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "执行器主键ID")
    private Integer jobGroup;
    
    @Schema(description = "任务，主键ID")
    private Integer jobId;
    
    @Schema(description = "执行器地址，本次执行的地址")
    private String executorAddress;
    
    @Schema(description = "执行器任务handler")
    private String executorHandler;
    
    @Schema(description = "执行器任务参数")
    private String executorParam;
    
    @Schema(description = "调度-时间")
    private Date triggerTime;
    
    @Schema(description = "调度-结果")
    private Integer triggerCode;
    
    @Schema(description = "执行-时间")
    private Date handleTime;
    
    @Schema(description = "执行-状态")
    private Integer handleCode;
    
    @Schema(description = "告警状态：0-默认，1-无需告警，2-告警成功，3-告警失败")
    private Integer alarmStatus;
}