package com.zero.app.util;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * MyBatis 测试工具类
 * 提供更优雅的会话管理和事务处理
 */
public class MyBatisTestUtil {
    
    private final SqlSessionFactory sqlSessionFactory;
    
    public MyBatisTestUtil(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    /**
     * 在事务中执行操作（自动提交）
     */
    public <T> T executeInTransaction(Function<SqlSession, T> function) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            try {
                T result = function.apply(session);
                session.commit();
                return result;
            } catch (Exception e) {
                session.rollback();
                throw new RuntimeException("Transaction rolled back due to exception", e);
            }
        }
    }
    
    /**
     * 在事务中执行操作（无返回值）
     */
    public void executeInTransaction(Consumer<SqlSession> consumer) {
        executeInTransaction(session -> {
            consumer.accept(session);
            return null;
        });
    }
    
    /**
     * 只读操作（不需要事务）
     */
    public <T> T executeReadOnly(Function<SqlSession, T> function) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            return function.apply(session);
        }
    }
    
    /**
     * 批量操作
     */
    public <T> T executeBatch(Function<SqlSession, T> function) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            try {
                T result = function.apply(session);
                session.commit();
                return result;
            } catch (Exception e) {
                session.rollback();
                throw new RuntimeException("Batch operation rolled back due to exception", e);
            }
        }
    }
    
    /**
     * 测试事务回滚
     */
    public void testRollback(Consumer<SqlSession> consumer) {
        try (SqlSession session = sqlSessionFactory.openSession(false)) {
            consumer.accept(session);
            // 故意不提交，测试回滚
            session.rollback();
            System.out.println("事务已回滚（测试目的）");
        }
    }
}