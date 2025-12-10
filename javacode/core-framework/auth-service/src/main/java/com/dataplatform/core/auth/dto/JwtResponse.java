package com.dataplatform.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "JWT响应DTO")
public class JwtResponse {
    
    @Schema(description = "访问令牌")
    private String accessToken;
    
    @Schema(description = "令牌类型")
    private String tokenType = "Bearer";
    
    @Schema(description = "过期时间（毫秒）")
    private Long expiresIn;
    
    @Schema(description = "用户信息")
    private UserInfo userInfo;
    
    public JwtResponse(String accessToken, Long expiresIn, UserInfo userInfo) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.userInfo = userInfo;
    }
}