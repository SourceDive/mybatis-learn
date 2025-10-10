package com.zero.mydebug.pure;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MyBatis 最小测试程序
 */
public class Level1CacheApp2 {
    
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

        SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
        sqlSessionManager.startManagedSession();


    }
}
