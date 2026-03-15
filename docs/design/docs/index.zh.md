# Mongo Plus 使用手册

<div class="hero">
    <div class="hero__visual">
        <img src="../assets/logo.svg" alt="Mongo Plus logo" width="240">
    </div>
    <div class="hero__body">
        <p class="hero__eyebrow">Spring Boot · MongoDB · 引擎优先访问</p>
        <p class="hero__title">一份面向 Mapper 路径与 Engine 路径的 MongoDB 接入手册。</p>
        <p class="hero__lead">
            Mongo Plus 是一个面向 Spring Boot 的 MongoDB 数据访问框架。它把引擎式查询入口、
            MyBatis-Plus 风格的 Wrapper / Mapper 模型、聚合能力、插件拦截器以及 Spring Boot
            Starter 组合在一起，用来降低 MongoDB 业务访问层的样板代码。
        </p>
        <div class="hero__chips">
            <span>MongoEngine</span>
            <span>Wrapper DSL</span>
            <span>MongoMapper</span>
            <span>APT</span>
            <span>拦截器</span>
        </div>
    </div>
</div>

本目录已经不再是旧项目的设计稿，而是当前 `mongo-plus` 仓库的正式使用手册。

## Mongo Plus 解决什么问题

- 用 `MongoEngine -> QueryService -> Query` 表达式组织查询链路。
- 用 `QueryWrapper` / `LambdaQueryWrapper` / `UpdateWrapper` 统一条件、排序、分页和更新语义。
- 用 `MongoMapper<T>` 提供 CRUD、分页、批量与聚合入口。
- 用 `MongoPlusInterceptor` 统一挂接查询、聚合和 Mapper 调用链扩展。
- 用 `mongo-plus-apt` 生成字段常量或 AutoMapper 相关输出，减少字符串字段名。

## 适合的接入方式

| 入口 | 适合场景 | 代表类型 |
| --- | --- | --- |
| Engine | 业务层偏好显式服务入口，希望调用链更接近查询服务 | `MongoEngine`、`QueryService` |
| Wrapper + Mapper | 需要 MyBatis-Plus 风格 CRUD 与分页能力 | `MongoMapper<T>`、`Wrappers` |
| Aggregation | 需要用 DSL 组合聚合管道 | `AggregationWrapper<T>` |
| Plugin | 需要审计、限流、追踪或短路执行 | `MongoPlusInterceptor` |

## 推荐阅读顺序

1. 先看 [环境与依赖](getting-started.md)，完成 Starter、JDK 与基础配置。
2. 再看 [快速开始](quick-start.md)，把实体、Mapper、Service 和配置串起来。
3. 根据项目习惯选择 [Engine 查询入口](engine-guide.md) 或 [Wrapper、分页与聚合](wrapper-guide.md)。
4. 需要代理扩展、APT 或自定义方法时，继续看 [Mapper 接口与扫描](mapper-guide.md) 与
   [插件与扩展点](plugin-guide.md)。
5. 需要验证 Starter 行为或跑集成示例时，查看 [示例模块](examples-guide.md)。

## 模块地图

| 模块 | 说明 |
| --- | --- |
| `mongo-plus-core` | 工具类、分页、元数据、ID 生成与基础模型 |
| `mongo-plus-wrapper` | Query / Update Wrapper 与 Lambda Wrapper |
| `mongo-plus-query` | 查询编译与 AST 相关支持 |
| `mongo-plus-aggregation` | 聚合 DSL 与阶段模型 |
| `mongo-plus-executor` | 基于 `MongoTemplate` 的执行层与 `MongoEngine` 入口 |
| `mongo-plus-mapper` | Mapper 契约定义 |
| `mongo-plus-plugin` | 拦截器 SPI 与调用上下文 |
| `mongo-plus-spring` | Starter、自动配置、Mapper 扫描与代理 |
| `mongo-plus-apt` | 字段常量 / AutoMapper 相关注解处理器 |
| `mongo-plus-examples` | Boot 2.x / 3.x 示例与集成验证 |

## 快速结论

!!! tip
    如果你只想用最短路径验证框架是否可用，优先按以下顺序执行：

    1. 按 [环境与依赖](getting-started.md) 引入 Starter。
    2. 按 [快速开始](quick-start.md) 创建实体和 Mapper。
    3. 运行根工程 `mvn test`。
    4. 需要集成验证时，再运行 `mvn -Pexamples test`。
