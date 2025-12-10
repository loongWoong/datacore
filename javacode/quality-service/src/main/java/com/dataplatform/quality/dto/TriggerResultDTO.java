package com.dataplatform.quality.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "触发任务执行结果DTO")
public class TriggerResultDTO {
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "消息")
    private String message;
    
    @Schema(description = "结果数量")
    private Integer resultCount;
}