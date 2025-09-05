# MyBatis 源码学习项目

这是一个用于学习和调试 MyBatis 源码的项目，包含了完整的 MyBatis 源码和测试程序。

## 项目结构

```
mybatis-learn/
├── mybatis-3/          # MyBatis 核心源码（子模块）
├── parent/             # MyBatis 父项目（子模块）
├── spring/             # MyBatis-Spring 集成（子模块）
├── mybatis-main-dir/   # 测试项目
├── pom.xml            # 父级 Maven 配置
├── build-and-test.sh  # 便捷构建脚本
└── README.md          # 项目说明
```

## 使用方式

### 1. JAR 依赖模式（传统方式）

```bash
# 修改 MyBatis 源码后需要手动编译
./build-and-test.sh
```

**特点：**
- 需要手动重新编译 MyBatis 源码
- 生成 JAR 包后测试项目才能使用
- 适合稳定的源码学习

### 2. 源码依赖模式（推荐）

```bash
# 直接依赖源码，修改后自动重新编译
./build-and-test.sh source
```

**特点：**
- 修改 MyBatis 源码后自动重新编译
- 不需要手动打包 JAR 文件
- 适合频繁修改源码的调试场景
- IDE 中可以直接调试源码

### 3. 只运行测试

```bash
# 当确定源码已经是最新编译时
./build-and-test.sh test
```

## 源码依赖 vs JAR 依赖

| 特性 | JAR 依赖模式 | 源码依赖模式 |
|------|-------------|-------------|
| 修改源码后 | 需要手动重新编译 | 自动重新编译 |
| 调试友好性 | 需要额外配置 | 直接支持 |
| 开发效率 | 较慢 | 更快 |
| 构建时间 | 较长 | 较短 |
| 适用场景 | 稳定学习 | 频繁调试 |

## 测试程序功能

测试程序演示了 MyBatis 的基本功能：

- ✅ 数据库初始化（H2 内存数据库）
- ✅ 用户实体类映射
- ✅ 注解方式的 Mapper 接口
- ✅ 完整的 CRUD 操作
- ✅ 事务管理
- ✅ 单元测试

## 运行测试

```bash
# 运行主程序
cd mybatis-main-dir
mvn exec:java -Dexec.mainClass="com.zero.app.Application"

# 运行单元测试
cd mybatis-main-dir
mvn test
```

## 源码学习建议

1. **从测试程序开始**：先理解测试程序如何使用 MyBatis
2. **跟踪执行流程**：在 IDE 中设置断点，跟踪 MyBatis 的执行流程
3. **修改源码验证**：尝试修改 MyBatis 源码，观察对测试程序的影响
4. **使用源码依赖模式**：提高开发效率

## 环境要求

- Java 8+
- Maven 3.6+
- Git

## 注意事项

- 使用源码依赖模式时，修改 MyBatis 源码后会自动重新编译
- 建议在 IDE 中打开整个项目，便于调试
- 测试程序使用 H2 内存数据库，无需额外安装数据库
