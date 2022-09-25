# flink sql

[flink 相关的概念](https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/concepts/overview/)

![flink concer](resource/levels_of_abstraction.svg)

flink table & sql

```xml
<dependency>
    <groupId>org.apache.flink</groupId>
    <artifactId>flink-table-planner-blink_2.11</artifactId>
    <version>1.13.5</version>
</dependency>
```

`flink-table-planner` 是 flink 将 Table API 和 sql 中对 table 的操作转换为可以执行的 flink 作业和优化执行过程的组件。有两个版本(blink 和 flink)

flink table 编程套路

1. 创建执行环境(ExecuteEnvironment) 和 表执行环境(TableEnvironment)
2. 获取表
3. 使用 Table API 或者 SQL 在表上执行查询插入等操作
4. 将结果表输出到外部系统
5. 调用 `execute` 执行作业

table Environment 主要作用是

1. 链接外部的系统(输入源和输出)
2. 向 catalog 中注册表或者从 catalog 中获取表
3. 执行 table API 或者 SQL 操作
4. 注册用户自定义的函数
5. 提供其他的配置功能
