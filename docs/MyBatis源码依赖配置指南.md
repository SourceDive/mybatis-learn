# MyBatis 源码依赖配置指南

## 背景

在搭建 MyBatis 源码学习环境时，我们需要让测试程序使用本地的 MyBatis 源码，而不是从 Maven 中央仓库下载的版本。这样可以：

1. **实时调试**：修改源码后立即生效
2. **源码学习**：深入理解 MyBatis 内部机制
3. **自定义开发**：基于源码进行二次开发

## 问题分析

### 错误做法 1：使用 Maven 中央仓库依赖

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4-my-debug-snapshot</version>
</dependency>
```

**问题**：
- 版本 `3.5.4-my-debug-snapshot` 在 Maven 中央仓库不存在
- 即使存在，也是远程版本，不是本地源码
- 无法进行源码调试和修改

### 错误做法 2：使用多模块项目但配置不当

```xml
<parent>
    <groupId>com.zero.learning</groupId>
    <artifactId>mybatis-learn-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>

<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <type>jar</type>
</dependency>
```

**问题**：
- 虽然配置了多模块项目，但依赖解析仍然指向外部仓库
- Maven 会尝试从中央仓库下载 `mybatis` 依赖
- 传递依赖（如 OGNL、Javassist）可能缺失

### 错误做法 3：配置文件错误

```xml
<mappers>
    <mapper class="com.zero.app.dao.UserDao"/>  <!-- 错误的类名 -->
</mappers>
```

**问题**：
- Mapper 类名不匹配，导致 `BindingException`
- MyBatis 无法找到对应的 Mapper 接口

## 正确的做法

### 方案 1：使用 system 作用域（推荐用于学习）

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4-my-debug-snapshot</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/../mybatis-3/target/mybatis-3.5.4-my-debug-snapshot.jar</systemPath>
</dependency>

<!-- 显式添加传递依赖 -->
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

**优点**：
- 明确指向本地 JAR 文件
- 版本控制清晰
- 适合源码学习场景

**缺点**：
- 需要手动管理传递依赖
- 每次修改源码后需要重新编译

### 方案 2：使用 Maven 多模块项目（推荐用于开发）

```xml
<!-- 父 pom.xml -->
<modules>
    <module>mybatis-3</module>
    <module>mybatis-main-dir</module>
</modules>

<!-- 子模块 pom.xml -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4-my-debug-snapshot</version>
</dependency>
```

**优点**：
- Maven 自动处理模块依赖
- 传递依赖自动解析
- 适合团队开发

**缺点**：
- 配置相对复杂
- 需要理解 Maven 多模块机制

## 推荐的工作流程

### 1. 环境准备

```bash
# 克隆 MyBatis 源码
git clone https://github.com/mybatis/mybatis-3.git
cd mybatis-3
git checkout 3.5.4  # 切换到稳定版本
```

### 2. 编译 MyBatis 源码

```bash
# 编译并安装到本地仓库
mvn clean install -DskipTests
```

### 3. 配置测试项目

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.4-my-debug-snapshot</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/../mybatis-3/target/mybatis-3.5.4-my-debug-snapshot.jar</systemPath>
</dependency>
```

### 4. 创建自动化脚本

```bash
#!/bin/bash
# build-and-test.sh

echo "🔨 编译 MyBatis 源码..."
cd mybatis-3
mvn clean install -DskipTests

echo "🧪 运行测试..."
cd ../mybatis-main-dir
mvn test
```

### 5. 验证配置

```bash
# 检查依赖树
mvn dependency:tree

# 应该看到：
# org.mybatis:mybatis:jar:3.5.4-my-debug-snapshot:system
```

## 常见问题解决

### 问题 1：`java: 程序包ognl不存在`

**原因**：缺少 MyBatis 的传递依赖

**解决**：
```xml
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

### 问题 2：`Type interface com.zero.app.UserMapper is not known to the MapperRegistry`

**原因**：MyBatis 配置文件中 Mapper 类名错误

**解决**：
```xml
<mappers>
    <mapper class="com.zero.app.UserMapper"/>  <!-- 正确的类名 -->
</mappers>
```

### 问题 3：`No suitable driver found for jdbc:h2:mem:testdb`

**原因**：H2 数据库驱动未加载

**解决**：
```java
// 在代码中显式加载驱动
Class.forName("org.h2.Driver");
```

### 问题 4：`Could not find artifact org.mybatis:mybatis:jar:3.5.4-my-debug-snapshot`

**原因**：本地 JAR 文件不存在

**解决**：
```bash
# 先编译 MyBatis 源码
cd mybatis-3
mvn clean install -DskipTests
```

## 最佳实践

### 1. 版本管理

- 使用有意义的版本号（如 `3.5.4-my-debug-snapshot`）
- 在源码中修改版本号以区分本地版本

### 2. 依赖管理

- 显式声明所有必要的传递依赖
- 使用 `dependency:tree` 验证依赖关系

### 3. 自动化构建

- 创建构建脚本自动化编译和测试流程
- 使用 Maven 插件简化操作

### 4. 调试配置

- 在 IDE 中配置源码路径
- 使用断点调试 MyBatis 内部逻辑

## 总结

MyBatis 源码学习环境的关键在于：

1. **明确依赖来源**：确保使用本地源码而不是远程依赖
2. **正确处理传递依赖**：显式声明 OGNL、Javassist 等依赖
3. **配置文件正确性**：确保 Mapper 类名和路径正确
4. **自动化流程**：创建脚本简化编译和测试过程

通过以上配置，你可以：
- 实时修改和调试 MyBatis 源码
- 深入理解 MyBatis 内部机制
- 基于源码进行二次开发
- 提高源码阅读效率

记住：**源码学习的关键是实践，多动手调试，多观察日志输出，才能真正理解框架的设计思想。**
