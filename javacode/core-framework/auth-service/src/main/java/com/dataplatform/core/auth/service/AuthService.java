package com.dataplatform.core.auth.service;

import com.dataplatform.core.auth.dto.JwtResponse;
import com.dataplatform.core.auth.dto.LoginRequest;

public interface AuthService {
    
    JwtResponse login(LoginRequest loginRequest);
    
    void logout(String token);
    
    Boolean validateToken(String token);
}