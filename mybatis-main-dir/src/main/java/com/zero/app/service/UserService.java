package com.zero.app.service;

import com.zero.app.dao.UserDao;
import com.zero.app.domain.User;
import com.zero.app.util.MyBatisTestUtil;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * 用户服务层
 * 演示如何在实际项目中组织代码
 */
public class UserService {
    
    private final MyBatisTestUtil mybatisUtil;
    
    public UserService(SqlSessionFactory sqlSessionFactory) {
        this.mybatisUtil = new MyBatisTestUtil(sqlSessionFactory);
    }
    
    /**
     * 查询所有用户（只读操作）
     */
    public List<User> findAllUsers() {
        return mybatisUtil.executeReadOnly(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectAll();
        });
    }
    
    /**
     * 根据ID查询用户
     */
    public User findUserById(Long id) {
        return mybatisUtil.executeReadOnly(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectById(id);
        });
    }
    
    /**
     * 创建用户（事务操作）
     */
    public User createUser(String name, String email) {
        return mybatisUtil.executeInTransaction(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            User user = new User(name, email);
            userDao.insert(user);
            return user;
        });
    }
    
    /**
     * 更新用户（事务操作）
     */
    public boolean updateUser(User user) {
        return mybatisUtil.executeInTransaction(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.update(user) > 0;
        });
    }
    
    /**
     * 删除用户（事务操作）
     */
    public boolean deleteUser(Long id) {
        return mybatisUtil.executeInTransaction(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.deleteById(id) > 0;
        });
    }
    
    /**
     * 批量创建用户
     */
    public void createUsersBatch(List<User> users) {
        mybatisUtil.executeBatch(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            for (User user : users) {
                userDao.insert(user);
            }
            return null;
        });
    }
    
    /**
     * 复杂业务操作示例：转移邮箱
     */
    public void transferEmail(Long fromUserId, Long toUserId) {
        mybatisUtil.executeInTransaction(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            
            // 获取源用户
            User fromUser = userDao.selectById(fromUserId);
            if (fromUser == null) {
                throw new RuntimeException("源用户不存在");
            }
            
            // 获取目标用户
            User toUser = userDao.selectById(toUserId);
            if (toUser == null) {
                throw new RuntimeException("目标用户不存在");
            }
            
            // 交换邮箱
            String tempEmail = fromUser.getEmail();
            fromUser.setEmail(toUser.getEmail());
            toUser.setEmail(tempEmail);
            
            // 更新两个用户
            userDao.update(fromUser);
            userDao.update(toUser);
            
            System.out.println("邮箱转移完成：" + fromUserId + " <-> " + toUserId);
        });
    }
}