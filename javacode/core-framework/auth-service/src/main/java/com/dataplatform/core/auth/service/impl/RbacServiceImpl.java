package com.dataplatform.core.auth.service.impl;

import com.dataplatform.core.auth.entity.Role;
import com.dataplatform.core.auth.entity.Permission;
import com.dataplatform.core.auth.mapper.RoleMapper;
import com.dataplatform.core.auth.mapper.PermissionMapper;
import com.dataplatform.core.auth.service.RbacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RbacServiceImpl implements RbacService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<Permission> permissions = permissionMapper.findPermissionsByUserId(userId);
        return permissions.stream()
                .anyMatch(permission -> permission.getPermissionCode().equals(permissionCode));
    }
    
    @Override
    public boolean hasRole(Long userId, String roleCode) {
        List<Role> roles = roleMapper.findRolesByUserId(userId);
        return roles.stream()
                .anyMatch(role -> role.getRoleCode().equals(roleCode));
    }
    
    @Override
    public List<String> getUserPermissions(Long userId) {
        List<Permission> permissions = permissionMapper.findPermissionsByUserId(userId);
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getUserRoles(Long userId) {
        List<Role> roles = roleMapper.findRolesByUserId(userId);
        return roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
    }
}