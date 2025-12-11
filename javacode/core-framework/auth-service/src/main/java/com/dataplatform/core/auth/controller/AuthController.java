package com.dataplatform.core.auth.controller;

import com.dataplatform.core.auth.dto.JwtResponse;
import com.dataplatform.core.auth.dto.LoginRequest;
import com.dataplatform.core.auth.service.AuthService;
import com.dataplatform.core.common.model.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户认证相关接口")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录并获取JWT令牌")
    public ResponseResult<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return ResponseResult.success("登录成功", jwtResponse);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出并清除JWT令牌")
    public ResponseResult<Void> logout(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        authService.logout(actualToken);
        return ResponseResult.success();
    }
    
    @GetMapping("/validate")
    @Operation(summary = "验证令牌", description = "验证JWT令牌是否有效")
    public ResponseResult<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Boolean isValid = authService.validateToken(actualToken);
        return ResponseResult.success("验证成功", isValid);
    }
}