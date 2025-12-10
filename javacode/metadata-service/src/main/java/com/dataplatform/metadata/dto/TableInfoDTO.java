package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "表信息DTO")
public class TableInfoDTO {
    
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "模式名称")
    private String schemaName;
    
    @Schema(description = "表名")
    private String tableName;
    
    @Schema(description = "中文表名")
    private String tableNameCn;
    
    @Schema(description = "表描述")
    private String tableDescription;
    
    @Schema(description = "业务域")
    private String businessDomain;
    
    @Schema(description = "数据源")
    private String dataSource;
    
    @Schema(description = "更新频率")
    private String updateFrequency;
    
    @Schema(description = "负责人/部门")
    private String owner;
    
    @Schema(description = "数据分层")
    private String dataLayer;
    
    @Schema(description = "记录数")
    private Long recordCount;
    
    @Schema(description = "最后更新时间")
    private Date lastUpdateTime;
    
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Schema(description = "更新时间")
    private Date updatedTime;
    
    @Schema(description = "字段列表")
    private List<ColumnInfoDTO> columns;
}