package com.zero.mydebug.pure.dao;

import com.zero.mydebug.pure.domain.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;

import java.util.List;

/**
 * 用户数据访问接口
 */
@CacheNamespace(
        implementation = PerpetualCache.class,
        eviction = LruCache.class,
        flushInterval = 60000,
        size = 1024,
        readWrite = true
)
public interface UserDao2 {

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
