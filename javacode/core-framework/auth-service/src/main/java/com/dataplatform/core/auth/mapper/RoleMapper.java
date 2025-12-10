package com.dataplatform.core.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.core.auth.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    
    @Select("SELECT r.* FROM sys_role r JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}