package com.zero.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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
 * MyBatis 单元测试
 */
public class AppTest extends TestCase {
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * 测试 MyBatis 基本功能
     */
    public void testMyBatis() throws Exception {
        // 初始化数据库
        initTestDatabase();
        
        // 测试 MyBatis 功能
        testMyBatisOperations();
    }
    
    /**
     * 初始化测试数据库
     */
    private void initTestDatabase() throws SQLException, IOException {
        Connection conn = DriverManager.getConnection(
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", 
            "sa", 
            ""
        );
        
        InputStream inputStream = Resources.getResourceAsStream("schema.sql");
        String sql = new String(inputStream.readAllBytes());
        String[] statements = sql.split(";");
        
        Statement stmt = conn.createStatement();
        for (String statement : statements) {
            if (statement.trim().length() > 0) {
                stmt.execute(statement.trim());
            }
        }
        
        stmt.close();
        conn.close();
    }
    
    /**
     * 测试 MyBatis 操作
     */
    private void testMyBatisOperations() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            
            // 测试查询所有用户
            List<User> users = userMapper.selectAll();
            assertTrue("应该至少有3个初始用户", users.size() >= 3);
            
            // 测试根据ID查询
            User user = userMapper.selectById(1L);
            assertNotNull("应该能找到ID为1的用户", user);
            assertEquals("用户ID应该为1", Long.valueOf(1L), user.getId());
            
            // 测试插入用户
            User newUser = new User("测试用户", "test@example.com");
            int insertResult = userMapper.insert(newUser);
            session.commit();
            assertEquals("插入应该成功", 1, insertResult);
            assertNotNull("新用户应该有ID", newUser.getId());
            
            // 测试更新用户
            newUser.setName("测试用户（已更新）");
            int updateResult = userMapper.update(newUser);
            session.commit();
            assertEquals("更新应该成功", 1, updateResult);
            
            // 测试删除用户
            int deleteResult = userMapper.deleteById(newUser.getId());
            session.commit();
            assertEquals("删除应该成功", 1, deleteResult);
            
            System.out.println("所有 MyBatis 测试通过！");
        }
    }
}
