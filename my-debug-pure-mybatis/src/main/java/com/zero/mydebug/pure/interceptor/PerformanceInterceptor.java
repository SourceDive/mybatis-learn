package com.zero.mydebug.pure.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * MyBatis 性能监控拦截器
 * 
 * 功能：
 * 1. 监控SQL执行时间
 * 2. 打印慢SQL
 * 3. 统计SQL执行次数
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", 
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", 
        args = {MappedStatement.class, Object.class})
})
public class PerformanceInterceptor implements Interceptor {
    
    private long slowSqlMillis = 1000; // 慢SQL阈值，默认1秒
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        String statementId = getStatementId(invocation);
        
        try {
            // 执行原方法
            Object result = invocation.proceed();
            
            // 计算执行时间
            long elapsedTime = System.currentTimeMillis() - startTime;
            
            // 打印执行信息
            String message = String.format("SQL [%s] 执行耗时：%d ms", statementId, elapsedTime);
            
            if (elapsedTime > slowSqlMillis) {
                System.err.println("【慢SQL警告】" + message);
            } else {
                System.out.println("【SQL监控】" + message);
            }
            
            return result;
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            System.err.println(String.format("【SQL异常】SQL [%s] 执行失败，耗时：%d ms，异常：%s", 
                statementId, elapsedTime, e.getMessage()));
            throw e;
        }
    }
    
    private String getStatementId(Invocation invocation) {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        return mappedStatement.getId();
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        String slowSqlMillisStr = properties.getProperty("slowSqlMillis");
        if (slowSqlMillisStr != null) {
            this.slowSqlMillis = Long.parseLong(slowSqlMillisStr);
        }
    }
}