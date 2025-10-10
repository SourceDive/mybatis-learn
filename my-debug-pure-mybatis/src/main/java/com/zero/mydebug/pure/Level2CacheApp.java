package com.zero.mydebug.pure;

import com.zero.mydebug.pure.dao.UserDao2;
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
 * MyBatis 测试二级缓存。
 */
public class Level2CacheApp {

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

        System.out.println("=== MyBatis 二级缓存测试开始 ===");

        // 第一次查询 - 使用第一个 SqlSession
        System.out.println("\n1. 第一次查询（第一个 SqlSession）：");
        try (SqlSession session1 = sqlSessionFactory.openSession()) {
            UserDao2 userDao1 = session1.getMapper(UserDao2.class);
            System.out.println("开始第一次查询...");
            List<User> users1 = userDao1.selectAll();
            System.out.println("第一次查询完成，结果数量：" + users1.size());
            for (User user : users1) {
                System.out.println("第一次查询结果：" + user);
            }
            // 关闭第一个 SqlSession，这样二级缓存才会生效
            System.out.println("关闭第一个 SqlSession，二级缓存应该已保存");
        }

        // 第二次查询 - 使用第二个 SqlSession（应该从二级缓存获取）
        System.out.println("\n2. 第二次查询（第二个 SqlSession，应该从二级缓存获取）：");
        try (SqlSession session2 = sqlSessionFactory.openSession()) {
            UserDao2 userDao2 = session2.getMapper(UserDao2.class);
            System.out.println("开始第二次查询...");
            List<User> users2 = userDao2.selectAll();
            System.out.println("第二次查询完成，结果数量：" + users2.size());
            for (User user : users2) {
                System.out.println("第二次查询结果：" + user);
            }
            System.out.println("如果第二次查询没有执行SQL，说明二级缓存生效了");
        }

        // 第三次查询 - 再次使用新的 SqlSession 验证缓存
        System.out.println("\n3. 第三次查询（第三个 SqlSession，进一步验证二级缓存）：");
        try (SqlSession session3 = sqlSessionFactory.openSession()) {
            UserDao2 userDao3 = session3.getMapper(UserDao2.class);
            System.out.println("开始第三次查询...");
            List<User> users3 = userDao3.selectAll();
            System.out.println("第三次查询完成，结果数量：" + users3.size());
            for (User user : users3) {
                System.out.println("第三次查询结果：" + user);
            }
        }

        System.out.println("\n=== MyBatis 测试完成 ===");
    }
}
