package com.zero.app;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户数据访问接口
 */
public interface UserMapper {
    
    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Long id);
    
    @Select("SELECT * FROM users")
    List<User> selectAll();
    
    @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
}
