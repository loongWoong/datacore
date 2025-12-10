package com.dataplatform.core.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.core.auth.entity.Permission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    
    @Select("SELECT p.* FROM sys_permission p JOIN sys_role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = #{roleId}")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);
    
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);
}