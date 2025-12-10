package com.dataplatform.core.auth.service;

import java.util.List;

public interface RbacService {
    
    boolean hasPermission(Long userId, String permissionCode);
    
    boolean hasRole(Long userId, String roleCode);
    
    List<String> getUserPermissions(Long userId);
    
    List<String> getUserRoles(Long userId);
}