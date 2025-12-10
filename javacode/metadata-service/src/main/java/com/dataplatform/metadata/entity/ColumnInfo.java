package com.dataplatform.metadata.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
@TableName("metadata_column_info")
@Schema(description = "字段信息元数据表")
public class ColumnInfo {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @TableField("table_id")
    @Schema(description = "表ID")
    private Long tableId;
    
    @TableField("column_name")
    @Schema(description = "字段名")
    private String columnName;
    
    @TableField("column_name_cn")
    @Schema(description = "中文字段名")
    private String columnNameCn;
    
    @TableField("column_description")
    @Schema(description = "字段描述")
    private String columnDescription;
    
    @TableField("data_type")
    @Schema(description = "数据类型")
    private String dataType;
    
    @TableField("is_primary_key")
    @Schema(description = "是否主键")
    private Integer isPrimaryKey;
    
    @TableField("is_nullable")
    @Schema(description = "是否可为空")
    private Integer isNullable;
    
    @TableField("column_order")
    @Schema(description = "字段顺序")
    private Integer columnOrder;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private Date updatedTime;
}