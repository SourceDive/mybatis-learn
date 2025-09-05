//package com.zero.app;
//
//import com.zero.app.dao.UserDao;
//import com.zero.app.domain.User;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * Spring Boot + MyBatis 集成示例
// *
// * 优势：
// * 1. 自动配置数据源和SqlSessionFactory
// * 2. 声明式事务管理
// * 3. 自动扫描Mapper接口
// * 4. 集成Spring生态系统
// */
//@SpringBootApplication
//@MapperScan("com.zero.app.dao")
//@EnableTransactionManagement
//public class SpringBootApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(SpringBootApplication.class, args);
//    }
//
//    @Bean
//    public CommandLineRunner demo(UserDao userDao) {
//        return args -> {
//            System.out.println("=== Spring Boot + MyBatis 测试开始 ===");
//
//            // 1. 查询所有用户
//            System.out.println("\n1. 查询所有用户：");
//            List<User> users = userDao.selectAll();
//            users.forEach(System.out::println);
//
//            // 2. 测试事务
//            System.out.println("\n2. 测试事务：");
//            try {
//                testTransaction(userDao);
//            } catch (Exception e) {
//                System.out.println("事务回滚: " + e.getMessage());
//            }
//
//            // 3. 最终查询
//            System.out.println("\n3. 事务测试后的用户列表：");
//            users = userDao.selectAll();
//            users.forEach(System.out::println);
//
//            System.out.println("\n=== Spring Boot + MyBatis 测试完成 ===");
//        };
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public void testTransaction(UserDao userDao) {
//        // 插入用户1
//        User user1 = new User("Spring用户1", "spring1@example.com");
//        userDao.insert(user1);
//        System.out.println("插入用户1: " + user1);
//
//        // 模拟异常
//        if (true) {
//            throw new RuntimeException("模拟异常，测试事务回滚");
//        }
//
//        // 这行不会执行
//        User user2 = new User("Spring用户2", "spring2@example.com");
//        userDao.insert(user2);
//    }
//}