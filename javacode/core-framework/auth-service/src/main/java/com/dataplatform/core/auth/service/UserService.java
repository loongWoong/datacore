package com.dataplatform.core.auth.service;

import com.dataplatform.core.auth.entity.User;
import com.dataplatform.core.auth.entity.Role;
import com.dataplatform.core.auth.entity.Permission;
import java.util.List;

public interface UserService {
    
    User findByUsername(String username);
    
    List<Role> findRolesByUserId(Long userId);
    
    List<Permission> findPermissionsByUserId(Long userId);
    
    User register(User user);
}