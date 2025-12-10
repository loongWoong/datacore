package com.dataplatform.scheduler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "分页结果DTO")
public class PageResult<T> {
    
    @Schema(description = "数据列表")
    private List<T> data;
    
    @Schema(description = "总记录数")
    private long total;
    
    @Schema(description = "当前页码")
    private int pageNum;
    
    @Schema(description = "每页大小")
    private int pageSize;
    
    public static <T> PageResult<T> of(List<T> data, long total, int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setData(data);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}