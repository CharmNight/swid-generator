SWID (Java Library)

[swid-generator](https://github.com/Labs64/swid-generator) 并没有兼容JDK17, 简单升级了下POM文件. 
核心修改:
- 替换 `javax` 相关依赖.

未考虑兼容问题:
- 未兼容JDK17 以下版本. 原因: 原项目支持到 JDK11
- 未严格测试. 原因: 测试学习使用
