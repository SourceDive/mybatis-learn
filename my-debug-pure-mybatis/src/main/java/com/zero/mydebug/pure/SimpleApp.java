package com.zero.mydebug.pure;

import com.zero.mydebug.pure.dao.UserDao;
import com.zero.mydebug.pure.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * MyBatis 最小测试程序
 */
public class SimpleApp {
    
    public static void main(String[] args) {
        try {
            // 初始化数据库
            initDatabase();
            
            // 运行 MyBatis 测试
            runMyBatisTest();
            
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
     * 运行 MyBatis 测试
     */
    private static void runMyBatisTest() throws IOException {
        // 加载 MyBatis 配置
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        // 获取 SqlSession
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 获取 Mapper
            UserDao userDao = session.getMapper(UserDao.class);
            
            System.out.println("=== MyBatis 测试开始 ===");
            
            // 1. 查询所有用户
            System.out.println("\n1. 查询所有用户：");
            List<User> users = userDao.selectAll();
            for (User user : users) {
                System.out.println(user);
            }
            // 再次查询
            List<User> users2 = userDao.selectAll();
            for (User user : users2) {
                System.out.println(user);
            }

//            // 2. 根据 ID 查询用户
//            System.out.println("\n2. 根据 ID 查询用户：");
//            User user = userDao.selectById(1L);
//            System.out.println("ID为1的用户：" + user);
//
//            // 3. 插入新用户
//            System.out.println("\n3. 插入新用户：");
//            User newUser = new User("赵六", "zhaoliu@example.com");
//            int insertResult = userDao.insert(newUser);
//            session.commit(); // 提交事务
//            System.out.println("插入结果：" + insertResult + "，新用户ID：" + newUser.getId());
//
//            // 4. 更新用户
//            System.out.println("\n4. 更新用户：");
//            newUser.setName("赵六（已更新）");
//            newUser.setEmail("zhaoliu_updated@example.com");
//            int updateResult = userDao.update(newUser);
//            session.commit(); // 提交事务
//            System.out.println("更新结果：" + updateResult);
//
//            // 5. 再次查询所有用户
//            System.out.println("\n5. 更新后的所有用户：");
//            users = userDao.selectAll();
//            for (User u : users) {
//                System.out.println(u);
//            }
//
//            // 6. 删除用户
//            System.out.println("\n6. 删除用户：");
//            int deleteResult = userDao.deleteById(newUser.getId());
//            session.commit(); // 提交事务
//            System.out.println("删除结果：" + deleteResult);
//
//            // 7. 最终查询所有用户
//            System.out.println("\n7. 删除后的所有用户：");
//            users = userDao.selectAll();
//            for (User u : users) {
//                System.out.println(u);
//            }
            
            System.out.println("\n=== MyBatis 测试完成 ===");
        }
    }
}
