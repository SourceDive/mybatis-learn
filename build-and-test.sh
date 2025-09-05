#!/bin/bash

# MyBatis 源码学习和测试脚本
# 使用方法：
# 1. 直接源码模式（推荐）：./build-and-test.sh
# 2. 只运行测试：./build-and-test.sh test
# 3. JAR 文件模式：./build-and-test.sh jar

set -e  # 遇到错误立即退出

echo "🚀 MyBatis 学习项目构建和测试脚本"
echo "=================================="

# 检查参数
if [ "$1" = "test" ]; then
    echo "📋 只运行测试..."
    cd mybatis-main-dir
    mvn test
    echo "✅ 测试完成！"
    exit 0
fi

if [ "$1" = "jar" ]; then
    echo "🔨 JAR 文件模式：先编译 MyBatis 源码生成 JAR..."
    cd mybatis-3
    mvn clean install -DskipTests
    echo "✅ MyBatis 源码编译完成！"
    
    echo ""
    echo "🧪 运行测试程序..."
    cd ../mybatis-main-dir
    mvn clean test
    echo "✅ 测试完成！"
    exit 0
fi

# 默认：直接源码模式
echo "🔧 直接源码模式：Maven 多模块项目自动处理依赖..."
echo "💡 这种方式修改 MyBatis 源码后会自动重新编译，无需手动生成 JAR"
echo ""

echo "🔨 编译整个项目..."
mvn clean compile test-compile
echo "✅ 源码依赖模式编译完成！"

echo ""
echo "🧪 运行测试..."
cd mybatis-main-dir
mvn test
echo "✅ 测试完成！"

echo ""
echo "🎉 所有步骤完成！"
echo ""
echo "💡 提示："
echo "   - 直接源码模式（推荐）: ./build-and-test.sh"
echo "   - 只运行测试: ./build-and-test.sh test"
echo "   - JAR 文件模式: ./build-and-test.sh jar"
echo "   - 运行主程序: cd mybatis-main-dir && mvn exec:java -Dexec.mainClass=\"com.zero.app.App\""
echo ""
echo "🔍 验证依赖来源: cd mybatis-main-dir && mvn dependency:tree"
