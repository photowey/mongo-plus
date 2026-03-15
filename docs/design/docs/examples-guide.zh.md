# 示例模块

`mongo-plus-examples` reactor 用来验证 Starter、Mapper 扫描、Wrapper、APT 与插件链路是否在真实
Spring Boot 应用中按预期工作。

## 模块矩阵

| 模块 | Starter | JDK 要求 | 用途 |
| --- | --- | --- | --- |
| `mongo-plus-examples-spring-boot-2x` | `mongo-plus-spring-boot-starter` | Java 11 | 验证 Boot 2.x Starter |
| `mongo-plus-examples-spring-boot-3x` | `mongo-plus-spring-boot3-starter` | Java 17 | 验证 Boot 3.x Starter |

## 为什么示例模块不在默认 reactor 里

根工程默认的 `mvn test` 只覆盖框架核心模块，目的是让日常反馈尽可能快、尽可能稳定。

示例模块依赖：

- Docker / Testcontainers
- 更完整的 Spring Boot 上下文
- Boot 2.x 和 Boot 3.x 双路径验证

因此它们被放进 `examples` profile，而不是默认验证路径。

## 运行前提

在跑示例模块前，请先确认：

- Docker 可用
- 本机 JDK 满足目标模块要求
- 如果包含 Boot 3 Starter，Maven Toolchains 已同时配置 JDK 11 和 JDK 17

推荐先检查：

```bash
docker ps
java -version
mvn -q -N toolchains:display-discovered-jdk-toolchains
```

## 常用命令

### 运行全部示例模块

```bash
mvn -Pexamples test
```

### 只跑 Boot 2.x 示例

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-2x test
```

### 只跑 Boot 3.x 示例

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

### 生成包含示例模块的 Allure 报告

```bash
make allure PROFILE=examples
```

## 这些示例覆盖了什么

当前示例模块主要覆盖以下能力：

- Starter 自动配置
- `@MongoMapperScan` 与配置项 `base-packages` 双扫描路径
- CRUD、分页、更新、删除、批量与聚合
- `@AutoMapper` 与注解处理器输出
- `MethodHandlerRegistryCustomizer` 自定义方法注册
- `MongoPlusInterceptor` 拦截器链路

## 什么时候必须跑示例模块

以下情况建议至少跑一次 `-Pexamples`：

- 修改了 `mongo-plus-spring` 自动配置
- 修改了 Mapper 代理或方法注册逻辑
- 修改了拦截器 SPI 或上下文模型
- 修改了 APT 或生成代码相关能力

## 与日常开发的关系

建议把验证分成两层：

1. 日常改动先跑 `mvn test`
2. 涉及 Starter、代理或示例链路时再跑 `mvn -Pexamples test`

这样可以兼顾反馈速度和覆盖面。
