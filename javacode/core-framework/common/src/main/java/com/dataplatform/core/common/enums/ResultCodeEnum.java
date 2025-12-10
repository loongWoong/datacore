package com.dataplatform.core.common.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不被允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    // 业务相关错误码
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_DISABLED(1003, "账号已被禁用"),
    USER_ACCOUNT_EXPIRED(1004, "账号已过期"),
    USER_CREDENTIALS_EXPIRED(1005, "凭证已过期"),
    USER_ACCOUNT_LOCKED(1006, "账号已被锁定"),
    
    ROLE_NOT_EXIST(2001, "角色不存在"),
    PERMISSION_DENIED(2002, "权限不足"),
    
    TOKEN_INVALID(3001, "令牌无效"),
    TOKEN_EXPIRED(3002, "令牌已过期"),
    TOKEN_EMPTY(3003, "令牌为空");
    
    private final Integer code;
    private final String message;
    
    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}