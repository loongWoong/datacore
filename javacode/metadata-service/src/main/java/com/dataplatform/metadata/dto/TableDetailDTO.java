package com.dataplatform.metadata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "表详情DTO")
public class TableDetailDTO extends TableInfoDTO {
    
    @Schema(description = "字段列表")
    private List<ColumnInfoDTO> columns;
}