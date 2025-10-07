package com.zero.mydebug.spring;

import com.zero.mydebug.spring.dao.UserDao;
import com.zero.mydebug.spring.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
public class MyBatisSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBatisSpringBootApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserDao userDao) {
        return args -> {
            System.out.println("=== Spring Boot + MyBatis 测试开始 ===");

            System.out.println("\n1. 查询所有用户：");
            List<User> users = userDao.selectAll();
            users.forEach(System.out::println);

            System.out.println("\n2. 测试事务：");
            try {
                testTransaction(userDao);
            } catch (Exception e) {
                System.out.println("事务回滚: " + e.getMessage());
            }

            System.out.println("\n3. 事务测试后的用户列表：");
            users = userDao.selectAll();
            users.forEach(System.out::println);

            System.out.println("\n=== Spring Boot + MyBatis 测试完成 ===");
        };
    }

    @Transactional(rollbackFor = Exception.class)
    public void testTransaction(UserDao userDao) {
        User user1 = new User("Spring用户1", "spring1@example.com");
        userDao.insert(user1);
        System.out.println("插入用户1: " + user1);

        if (true) {
            throw new RuntimeException("模拟异常，测试事务回滚");
        }

        User user2 = new User("Spring用户2", "spring2@example.com");
        userDao.insert(user2);
    }
}