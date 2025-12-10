package com.dataplatform.core.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.core.auth.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
}