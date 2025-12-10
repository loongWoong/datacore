package com.dataplatform.metadata.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("metadata_table_info")
@Schema(description = "表信息元数据表")
public class TableInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("schema_name")
    @Schema(description = "模式名称")
    private String schemaName;
    
    @TableField("table_name")
    @Schema(description = "表名")
    private String tableName;
    
    @TableField("table_name_cn")
    @Schema(description = "中文表名")
    private String tableNameCn;
    
    @TableField("table_description")
    @Schema(description = "表描述")
    private String tableDescription;
    
    @TableField("business_domain")
    @Schema(description = "业务域")
    private String businessDomain;
    
    @TableField("data_source")
    @Schema(description = "数据源")
    private String dataSource;
    
    @TableField("update_frequency")
    @Schema(description = "更新频率")
    private String updateFrequency;
    
    @TableField("owner")
    @Schema(description = "负责人/部门")
    private String owner;
    
    @TableField("data_layer")
    @Schema(description = "数据分层")
    private String dataLayer;
    
    @TableField("record_count")
    @Schema(description = "记录数")
    private Long recordCount;
    
    @TableField("last_update_time")
    @Schema(description = "最后更新时间")
    private Date lastUpdateTime;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}