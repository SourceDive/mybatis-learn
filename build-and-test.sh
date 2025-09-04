#!/bin/bash

# MyBatis 源码学习和测试脚本
# 使用方法：
# 1. 修改 MyBatis 源码后运行：./build-and-test.sh
# 2. 只运行测试：./build-and-test.sh test

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

echo "🔨 步骤 1: 编译 MyBatis 源码..."
cd mybatis-3
mvn clean install -DskipTests
echo "✅ MyBatis 源码编译完成！"

echo ""
echo "🧪 步骤 2: 运行测试程序..."
cd ../mybatis-main-dir
mvn clean test
echo "✅ 测试完成！"

echo ""
echo "🎉 所有步骤完成！"
echo ""
echo "💡 提示："
echo "   - 修改 MyBatis 源码后运行: ./build-and-test.sh"
echo "   - 只运行测试: ./build-and-test.sh test"
echo "   - 运行主程序: cd mybatis-main-dir && mvn exec:java -Dexec.mainClass=\"com.zero.app.App\""
