package com.dataplatform.core.auth.service.impl;

import com.dataplatform.core.auth.dto.JwtResponse;
import com.dataplatform.core.auth.dto.LoginRequest;
import com.dataplatform.core.auth.dto.UserInfo;
import com.dataplatform.core.auth.entity.User;
import com.dataplatform.core.auth.entity.Role;
import com.dataplatform.core.auth.entity.Permission;
import com.dataplatform.core.auth.service.AuthService;
import com.dataplatform.core.auth.service.UserService;
import com.dataplatform.core.auth.util.JwtUtil;
import com.dataplatform.core.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        // 1. 查找用户
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException(1001, "用户不存在");
        }
        
        // 2. 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(1002, "密码错误");
        }
        
        // 3. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(1003, "账号已被禁用");
        }
        
        // 4. 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());
        
        // 5. 获取用户角色和权限
        List<Role> roles = userService.findRolesByUserId(user.getId());
        List<Permission> permissions = userService.findPermissionsByUserId(user.getId());
        
        // 6. 构造用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roles.stream().map(Role::getRoleCode).collect(Collectors.toList()));
        userInfo.setPermissions(permissions.stream().map(Permission::getPermissionCode).collect(Collectors.toList()));
        
        // 7. 将令牌存储到Redis中
        redisTemplate.opsForValue().set("token:" + token, user.getId(), jwtUtil.getExpiration(), TimeUnit.SECONDS);
        
        return new JwtResponse(token, jwtUtil.getExpiration(), userInfo);
    }
    
    @Override
    public void logout(String token) {
        // 从Redis中移除令牌
        redisTemplate.delete("token:" + token);
    }
    
    @Override
    public Boolean validateToken(String token) {
        // 检查令牌是否在Redis中存在
        return redisTemplate.hasKey("token:" + token);
    }
}