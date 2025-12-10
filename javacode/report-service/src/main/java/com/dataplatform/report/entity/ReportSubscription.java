package com.dataplatform.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("report_subscription")
@Schema(description = "报表订阅表")
public class ReportSubscription {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("report_id")
    @Schema(description = "报表ID")
    private Long reportId;
    
    @TableField("subscriber")
    @Schema(description = "订阅人")
    private String subscriber;
    
    @TableField("subscription_type")
    @Schema(description = "订阅类型（EMAIL/WEBHOOK）")
    private String subscriptionType;
    
    @TableField("subscription_config")
    @Schema(description = "订阅配置（JSON格式）")
    private String subscriptionConfig;
    
    @TableField("is_active")
    @Schema(description = "是否激活")
    private Integer isActive;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}