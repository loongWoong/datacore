package com.dataplatform.core.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dataplatform.core.auth.entity.User;
import com.dataplatform.core.auth.entity.Role;
import com.dataplatform.core.auth.entity.Permission;
import com.dataplatform.core.auth.mapper.UserMapper;
import com.dataplatform.core.auth.mapper.RoleMapper;
import com.dataplatform.core.auth.mapper.PermissionMapper;
import com.dataplatform.core.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }
    
    @Override
    public List<Permission> findPermissionsByUserId(Long userId) {
        return permissionMapper.findPermissionsByUserId(userId);
    }
    
    @Override
    public User register(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(1); // 默认启用
        
        userMapper.insert(user);
        return user;
    }
}