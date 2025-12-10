package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新执行器DTO")
public class UpdateJobGroupDTO {
    
    @Schema(description = "执行器AppName")
    private String appname;
    
    @Schema(description = "执行器名称")
    private String title;
    
    @Schema(description = "排序")
    private Integer order;
    
    @Schema(description = "执行器地址类型：0=自动注册、1=手动录入")
    private Integer addressType;
    
    @Schema(description = "执行器地址列表，多地址逗号分隔")
    private String addressList;
}