package com.dataplatform.core.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "统一响应结果")
public class ResponseResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "响应码")
    private Integer code;
    
    @Schema(description = "响应消息")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    public ResponseResult() {}
    
    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "操作成功");
    }
    
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "操作成功", data);
    }
    
    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(200, message, data);
    }
    
    public static <T> ResponseResult<T> error() {
        return new ResponseResult<>(500, "操作失败");
    }
    
    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(500, message);
    }
    
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(code, message);
    }
}