package com.zero.mydebug.pure;

import com.zero.mydebug.pure.domain.User;
import com.zero.mydebug.pure.service.UserService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * MyBatis 高级特性测试程序
 * 
 * 演示内容：
 * 1. 服务层封装
 * 2. 批量操作
 * 3. 复杂业务逻辑
 * 4. 性能监控
 * 5. 更好的代码组织
 */
public class AdvancedApplication {
    
    public static void main(String[] args) {
        try {
            // 初始化数据库
            initDatabase();
            
            // 初始化MyBatis（使用增强配置）
            SqlSessionFactory sqlSessionFactory = initMyBatis();
            
            // 创建服务
            UserService userService = new UserService(sqlSessionFactory);
            
            // 运行各种测试
            System.out.println("=== MyBatis 高级特性测试 ===\n");
            
            testBasicOperations(userService);
            testBatchOperations(userService);
            testComplexBusinessLogic(userService);
            testPerformance(userService);
            
            System.out.println("\n=== 测试完成 ===");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initDatabase() throws Exception {
        Class.forName("org.h2.Driver");
        
        try (Connection conn = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
             Statement stmt = conn.createStatement()) {
            
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
            for (String statement : statements) {
                if (statement.trim().length() > 0) {
                    stmt.execute(statement.trim());
                }
            }
            
            System.out.println("数据库初始化完成！\n");
        }
    }
    
    private static SqlSessionFactory initMyBatis() throws IOException {
        // 使用增强配置文件
        String resource = "mybatis-config-enhanced.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            return new SqlSessionFactoryBuilder().build(inputStream);
        }
    }
    
    /**
     * 测试基本操作
     */
    private static void testBasicOperations(UserService userService) {
        System.out.println("1. 基本CRUD操作测试");
        System.out.println("-------------------");
        
        // 查询所有用户
        List<User> users = userService.findAllUsers();
        System.out.println("初始用户列表：");
        users.forEach(user -> System.out.println("  " + user));
        
        // 创建新用户
        User newUser = userService.createUser("服务层用户", "service@example.com");
        System.out.println("\n创建新用户：" + newUser);
        
        // 更新用户
        newUser.setName("服务层用户（已更新）");
        boolean updated = userService.updateUser(newUser);
        System.out.println("更新结果：" + updated);
        
        // 删除用户
        boolean deleted = userService.deleteUser(newUser.getId());
        System.out.println("删除结果：" + deleted);
        
        System.out.println();
    }
    
    /**
     * 测试批量操作
     */
    private static void testBatchOperations(UserService userService) {
        System.out.println("2. 批量操作测试");
        System.out.println("---------------");
        
        // 批量创建用户
        List<User> batchUsers = Arrays.asList(
            new User("批量用户1", "batch1@example.com"),
            new User("批量用户2", "batch2@example.com"),
            new User("批量用户3", "batch3@example.com"),
            new User("批量用户4", "batch4@example.com"),
            new User("批量用户5", "batch5@example.com")
        );
        
        long startTime = System.currentTimeMillis();
        userService.createUsersBatch(batchUsers);
        long endTime = System.currentTimeMillis();
        
        System.out.println("批量创建5个用户，耗时：" + (endTime - startTime) + "ms");
        
        // 查询确认
        List<User> allUsers = userService.findAllUsers();
        System.out.println("当前用户总数：" + allUsers.size());
        
        System.out.println();
    }
    
    /**
     * 测试复杂业务逻辑
     */
    private static void testComplexBusinessLogic(UserService userService) {
        System.out.println("3. 复杂业务逻辑测试");
        System.out.println("-------------------");
        
        try {
            // 获取两个用户
            List<User> users = userService.findAllUsers();
            if (users.size() >= 2) {
                User user1 = users.get(0);
                User user2 = users.get(1);
                
                System.out.println("转移前：");
                System.out.println("  用户1：" + user1);
                System.out.println("  用户2：" + user2);
                
                // 执行邮箱转移
                userService.transferEmail(user1.getId(), user2.getId());
                
                // 重新查询
                user1 = userService.findUserById(user1.getId());
                user2 = userService.findUserById(user2.getId());
                
                System.out.println("转移后：");
                System.out.println("  用户1：" + user1);
                System.out.println("  用户2：" + user2);
            }
        } catch (Exception e) {
            System.out.println("业务操作失败：" + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * 测试性能
     */
    private static void testPerformance(UserService userService) {
        System.out.println("4. 性能测试");
        System.out.println("-----------");
        
        // 测试查询性能
        int iterations = 100;
        long totalTime = 0;
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            userService.findAllUsers();
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        double avgTimeMs = totalTime / 1_000_000.0 / iterations;
        System.out.println(String.format("查询所有用户平均耗时：%.2f ms (%d次测试)", avgTimeMs, iterations));
        
        // 测试单个查询性能
        totalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            userService.findUserById(1L);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        
        avgTimeMs = totalTime / 1_000_000.0 / iterations;
        System.out.println(String.format("根据ID查询用户平均耗时：%.2f ms (%d次测试)", avgTimeMs, iterations));
    }
}