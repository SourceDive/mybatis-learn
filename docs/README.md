# MyBatis 源码学习环境文档

## 文档索引

### 📚 核心文档

1. **[MyBatis源码依赖配置指南](./MyBatis源码依赖配置指南.md)**
   - 详细的配置指南
   - 错误做法 vs 正确做法
   - 最佳实践推荐

2. **[问题分析与解决过程](./问题分析与解决过程.md)**
   - 问题回顾和分析
   - 解决过程详解
   - 经验教训总结

3. **[快速参考指南](./快速参考指南.md)**
   - 快速开始指南
   - 常用命令和配置
   - 常见问题解决

## 问题总结

### 主要问题
**测试程序依赖的是外部 MyBatis 而不是本地源码**

### 根本原因
1. **Maven 依赖解析机制误解**：以为多模块项目会自动使用本地源码
2. **配置不当**：没有明确指定依赖来源
3. **传递依赖缺失**：OGNL、Javassist 等依赖没有正确配置

### 解决方案
1. **使用 system 作用域**：明确指向本地 JAR 文件
2. **显式声明传递依赖**：添加 OGNL、Javassist 依赖
3. **修复配置文件**：确保 Mapper 类名正确

## 关键配置

### 正确的 pom.xml 配置

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4-my-debug-snapshot</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/../mybatis-3/target/mybatis-3.5.4-my-debug-snapshot.jar</systemPath>
</dependency>

<!-- 传递依赖 -->
<dependency>
    <groupId>ognl</groupId>
    <artifactId>ognl</artifactId>
    <version>3.2.12</version>
</dependency>
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.26.0-GA</version>
</dependency>
```

### 验证方法

```bash
# 检查依赖来源
mvn dependency:tree

# 应该看到：
# org.mybatis:mybatis:jar:3.5.4-my-debug-snapshot:system
```

## 快速开始

### 1. 编译 MyBatis 源码
```bash
cd mybatis-3
mvn clean install -DskipTests
```

### 2. 运行测试
```bash
cd mybatis-main-dir
mvn test
```

### 3. 使用自动化脚本
```bash
./build-and-test.sh
```

## 学习建议

1. **先阅读配置指南**：理解正确的配置方式
2. **分析问题过程**：了解常见错误和解决方法
3. **使用快速参考**：日常开发中的常用命令和配置
4. **实践验证**：动手操作，验证配置正确性

## 注意事项

- 每次修改 MyBatis 源码后都要重新编译
- 确保 JAR 文件路径正确
- 定期验证依赖来源
- 保持配置文件同步

## 扩展阅读

- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
- [Maven 依赖管理](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
- [源码学习最佳实践](https://github.com/mybatis/mybatis-3)

---

**记住**：源码学习的关键是实践，多动手调试，多观察日志输出，才能真正理解框架的设计思想。
