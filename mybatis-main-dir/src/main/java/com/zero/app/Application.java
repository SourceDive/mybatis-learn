package com.zero.app;

import com.zero.app.dao.UserDao;
import com.zero.app.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;

/**
 * MyBatis 最小测试程序 - 优化版
 * 
 * 优化点：
 * 1. 添加了日志支持
 * 2. 使用try-with-resources自动管理资源
 * 3. 添加了事务管理辅助方法
 * 4. 支持Lambda表达式简化代码
 */
public class Application {
    
    private static SqlSessionFactory sqlSessionFactory;
    
    static {
        // 设置MyBatis使用JDK日志
        LogFactory.useJdkLogging();
    }
    
    public static void main(String[] args) {
        try {
            // 初始化数据库
            initDatabase();
            
            // 初始化MyBatis
            initMyBatis();
            
            // 运行 MyBatis 测试
            runMyBatisTest();
            
            // 运行事务测试
            runTransactionTest();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 初始化 H2 数据库
     */
    private static void initDatabase() throws SQLException, IOException, ClassNotFoundException {
        // 加载 H2 驱动
        Class.forName("org.h2.Driver");
        
        // 连接 H2 数据库
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", 
            "sa", 
            ""
        );
        
        // 执行建表脚本
        InputStream inputStream = Resources.getResourceAsStream("schema.sql");
        StringBuilder sql = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            sql.append(new String(buffer, 0, bytesRead));
        }
        inputStream.close();
        String[] statements = sql.toString().split(";");
        
        Statement stmt = conn.createStatement();
        for (String statement : statements) {
            if (statement.trim().length() > 0) {
                stmt.execute(statement.trim());
            }
        }
        
        stmt.close();
        conn.close();
        System.out.println("数据库初始化完成！");
    }
    
    /**
     * 初始化 MyBatis
     */
    private static void initMyBatis() throws IOException {
        String resource = "mybatis-config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            
            // 获取配置并打印一些调试信息
            Configuration config = sqlSessionFactory.getConfiguration();
            System.out.println("MyBatis 配置初始化完成！");
            System.out.println("默认执行器类型: " + config.getDefaultExecutorType());
            System.out.println("缓存启用状态: " + config.isCacheEnabled());
            System.out.println("懒加载启用状态: " + config.isLazyLoadingEnabled());
        }
    }
    
    /**
     * 执行数据库操作（自动管理SqlSession）
     */
    private static <T> T executeWithSession(SqlSessionFunction<T> function) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            T result = function.apply(session);
            session.commit();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("数据库操作失败", e);
        }
    }
    
    /**
     * 执行数据库操作（无返回值）
     */
    private static void executeWithSession(Consumer<SqlSession> consumer) {
        executeWithSession(session -> {
            consumer.accept(session);
            return null;
        });
    }
    
    @FunctionalInterface
    private interface SqlSessionFunction<T> {
        T apply(SqlSession session) throws Exception;
    }
    
    /**
     * 运行 MyBatis 测试
     */
    private static void runMyBatisTest() throws IOException {
        System.out.println("=== MyBatis 测试开始 ===");
        
        // 1. 查询所有用户
        System.out.println("\n1. 查询所有用户：");
        List<User> users = executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectAll();
        });
        users.forEach(System.out::println);
        
        // 2. 根据 ID 查询用户
        System.out.println("\n2. 根据 ID 查询用户：");
        User user = executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectById(1L);
        });
        System.out.println("ID为1的用户：" + user);
        
        // 3. 插入新用户
        System.out.println("\n3. 插入新用户：");
        User newUser = new User("赵六", "zhaoliu@example.com");
        executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            int result = userDao.insert(newUser);
            System.out.println("插入结果：" + result + "，新用户ID：" + newUser.getId());
        });
        
        // 4. 更新用户
        System.out.println("\n4. 更新用户：");
        newUser.setName("赵六（已更新）");
        newUser.setEmail("zhaoliu_updated@example.com");
        executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            int result = userDao.update(newUser);
            System.out.println("更新结果：" + result);
        });
        
        // 5. 再次查询所有用户
        System.out.println("\n5. 更新后的所有用户：");
        users = executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectAll();
        });
        users.forEach(System.out::println);
        
        // 6. 删除用户
        System.out.println("\n6. 删除用户：");
        final Long userIdToDelete = newUser.getId();
        executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            int result = userDao.deleteById(userIdToDelete);
            System.out.println("删除结果：" + result);
        });
        
        // 7. 最终查询所有用户
        System.out.println("\n7. 删除后的所有用户：");
        users = executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectAll();
        });
        users.forEach(System.out::println);
        
        System.out.println("\n=== MyBatis 测试完成 ===");
    }
    
    /**
     * 运行事务测试
     */
    private static void runTransactionTest() {
        System.out.println("\n=== 事务测试开始 ===");
        
        try {
            // 测试事务回滚
            executeWithSession(session -> {
                UserDao userDao = session.getMapper(UserDao.class);
                
                // 插入一个用户
                User user1 = new User("事务测试用户1", "tx_test1@example.com");
                userDao.insert(user1);
                System.out.println("插入用户1: " + user1);
                
                // 故意抛出异常，测试事务回滚
                if (true) {
                    throw new RuntimeException("模拟异常，测试事务回滚");
                }
                
                // 这行代码不会执行
                User user2 = new User("事务测试用户2", "tx_test2@example.com");
                userDao.insert(user2);
            });
        } catch (Exception e) {
            System.out.println("事务已回滚: " + e.getMessage());
        }
        
        // 验证事务回滚效果
        System.out.println("\n验证事务回滚后的用户列表：");
        List<User> users = executeWithSession(session -> {
            UserDao userDao = session.getMapper(UserDao.class);
            return userDao.selectAll();
        });
        users.forEach(System.out::println);
        
        System.out.println("\n=== 事务测试完成 ===");
    }
}
