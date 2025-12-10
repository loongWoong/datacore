package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "创建表信息DTO")
public class CreateTableDTO {
    
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
    
    @Schema(description = "字段列表")
    private List<CreateColumnDTO> columns;
}