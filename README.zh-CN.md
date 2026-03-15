[English](README.md) | 简体中文

# `mongo-plus`

`mongo-plus` 是一个面向 Spring Boot 的 MongoDB 数据访问框架，将引擎式查询入口与
受 MyBatis-Plus 启发的 wrapper / mapper 模型结合在一起。
它让业务代码能够以 `MongoEngine -> QueryService -> Query` 的方式组织调用，同时在
Spring Data MongoDB 之上保留流式 wrapper、Lambda 安全字段引用、聚合辅助能力、
Mapper 风格接口以及 Spring Boot Starter 集成。

## 特性概览

- 引擎式运行时入口：`MongoEngine -> QueryService -> EngineQuery`
- 流式查询与更新 wrapper：`QueryWrapper`、`LambdaQueryWrapper`、`UpdateWrapper`、`LambdaUpdateWrapper`
- 基于 AST 的查询编译与聚合流水线建模
- Mapper 风格的 CRUD、分页、批量操作与聚合入口
- 基于元数据的主键处理与更新执行
- 面向 mapper、query、aggregation 调用链的执行拦截器 SPI
- Spring Boot 集成，提供自动配置的 engine、mapper 扫描与动态代理分发
- 基于 APT 的编译期字段 / 索引常量生成支持
- 默认提供 Spring Boot 2.x starter，Java 17+ 下提供 Spring Boot 3 starter

## 模块说明

| 模块                     | 职责                                                                                       |
|--------------------------|--------------------------------------------------------------------------------------------|
| `mongo-plus-annotation`  | 框架注解                                                                                   |
| `mongo-plus-bom`         | 用于对齐 MongoPlus 已发布构件版本的 BOM                                                    |
| `mongo-plus-core`        | 核心工具、元数据、分页、地理模型、ID 生成                                                  |
| `mongo-plus-dsl`         | DSL 抽象与 AST 模型                                                                        |
| `mongo-plus-wrapper`     | 查询 / 更新 wrapper 实现                                                                   |
| `mongo-plus-query`       | 查询构建器与 AST 编译支持                                                                  |
| `mongo-plus-aggregation` | 聚合阶段模型与 wrapper DSL                                                                 |
| `mongo-plus-optimizer`   | 聚合 / 查询优化规则                                                                        |
| `mongo-plus-executor`    | 基于 `MongoTemplate` 的执行层，以及 `MongoEngine` / `QueryService` 集成 API               |
| `mongo-plus-mapper`      | Mapper 契约                                                                                |
| `mongo-plus-plugin`      | 拦截器 SPI、调用上下文模型以及执行扩展点                                                   |
| `mongo-plus-spring`      | Spring Boot 自动配置与 starters                                                            |
| `mongo-plus-apt`         | 用于生成常量的注解处理器                                                                   |
| `mongo-plus-examples`    | 示例应用与集成风格样例测试                                                                 |

### Reactor 结构

```text
mongo-plus
├── mongo-plus-annotation
├── mongo-plus-aggregation
├── mongo-plus-apt
├── mongo-plus-bom
├── mongo-plus-core
├── mongo-plus-dsl
├── mongo-plus-executor
├── mongo-plus-mapper
├── mongo-plus-optimizer
├── mongo-plus-plugin
├── mongo-plus-query
├── mongo-plus-spring
│   ├── mongo-plus-spring-boot-autoconfigure
│   ├── mongo-plus-spring-boot-starter
│   └── mongo-plus-spring-boot3-starter
├── mongo-plus-wrapper
└── mongo-plus-examples            # 通过 -Pexamples 启用
    ├── mongo-plus-examples-spring-boot-2x
    └── mongo-plus-examples-spring-boot-3x
```

## 安装

### Spring Boot 2.x

```xml
<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-spring-boot-starter</artifactId>
    <version>${mongo-plus.version}</version>
</dependency>
```

### Spring Boot 3.x

如果项目基于 Java 17 或更高版本，请使用 Boot 3 starter：

```xml
<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-spring-boot3-starter</artifactId>
    <version>${mongo-plus.version}</version>
</dependency>
```

## 快速开始

### 实体

```java
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Field("user_name")
    private String userName;

    private Integer age;
    private String email;
}
```

### 引擎优先的服务调用方式

```java
import java.util.List;

import io.github.photowey.mongoplus.executor.integration.engine.MongoEngine;

public class UserService {

    private final MongoEngine mongoEngine;

    public UserService(MongoEngine mongoEngine) {
        this.mongoEngine = mongoEngine;
    }

    public List<UserDocument> findAdults() {
        return this.mongoEngine
            .queryService()
            .createLambdaQuery(UserDocument.class)
            .gte(UserDocument::getAge, 18)
            .orderByDesc(UserDocument::getId)
            .list();
    }
}
```

### Mapper

```java
import io.github.photowey.mongoplus.mapper.MongoMapper;

public interface UserMapper extends MongoMapper<UserDocument> {
}
```

### 基于 Mapper 的服务调用方式

```java
import java.util.List;

import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;

public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserDocument findById(Long id) {
        return this.userMapper.selectById(id);
    }

    public List<UserDocument> findAdults() {
        return this.userMapper.selectList(
            Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
                .gte(UserDocument::getAge, 18)
                .orderByDesc(UserDocument::getId)
        );
    }
}
```

## 当前 API 能力

### Engine 查询入口

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getUserName, "photowey")
    .gte(UserDocument::getAge, 18)
    .orderByDesc(UserDocument::getId)
    .list();
```

### Engine 元数据查询入口

```java
UserDocument user = mongoEngine
    .queryService()
    .createQuery(UserDocument.class)
    .eq(UserDocumentColumns.USER_NAME, "photowey")
    .one();
```

### 插件拦截器

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.photowey.mongoplus.plugin.context.InvocationContext;
import io.github.photowey.mongoplus.plugin.context.QueryExecutionContext;
import io.github.photowey.mongoplus.plugin.interceptor.Invocation;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;

@Configuration
public class MongoPlusPluginConfiguration {

    @Bean
    public MongoPlusInterceptor tracingInterceptor() {
        return new MongoPlusInterceptor() {
            @Override
            public int getOrder() {
                return 10;
            }

            @Override
            public boolean supports(InvocationContext context) {
                return context instanceof QueryExecutionContext;
            }

            @Override
            public Object intercept(Invocation invocation) throws Throwable {
                QueryExecutionContext context = (QueryExecutionContext) invocation.getContext();
                // Inspect the operation, wrapper, or page metadata before execution.
                return invocation.proceed();
            }
        };
    }
}
```

Spring Boot Starter 自动配置会收集所有 `MongoPlusInterceptor` Bean，按照 `getOrder()`
排序后应用到 mapper 分发、query 执行和 aggregation 执行流程中。
更多说明见 [docs/plugin-interceptors.md](docs/plugin-interceptors.md)。

### Query wrappers

```java
List<UserDocument> users = userMapper.selectList(
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .eq(UserDocument::getUserName, "photowey")
        .gte(UserDocument::getAge, 18)
        .orderByDesc(UserDocument::getId)
        .limit(10L)
);
```

### Update wrappers

```java
boolean updated = userMapper.update(
    null,
    Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
        .eq(UserDocument::getId, 1L)
        .set(UserDocument::getEmail, "next@example.com")
        .inc(UserDocument::getAge, 1)
);
```

### 分页

```java
Page<UserDocument> page = new Page<>(1L, 10L, true);
Page<UserDocument> result = userMapper.selectPage(
    page,
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .orderByDesc(UserDocument::getId)
);
```

### 聚合

```java
AggregationWrapper<UserDocument> wrapper = AggregationWrapper.aggregation(UserDocument.class)
    .match(query -> query.gte(UserDocument::getAge, 18))
    .group(UserDocument::getAge)
    .count("total");
```

### 批量操作

```java
BatchResult result = userMapper.insertBatch(users);
```

## 构建与验证

### 默认本地验证路径

根工程默认的本地验证命令是：

```bash
mvn test
```

这条路径保持可移植性，不依赖 examples reactor。

### Maven Toolchains（boot3-starter 必需）

`mongo-plus-spring-boot3-starter` 模块目标 JDK 为 17，并通过 Maven Toolchains 构建。
项目其余部分使用 JDK 11。你需要在 `~/.m2/toolchains.xml` 中同时配置两个 JDK：

1. 复制模板：`cp toolchains.xml.template ~/.m2/toolchains.xml`
2. 编辑 `~/.m2/toolchains.xml`，将 `<jdkHome>` 指向本机的 JDK 11 与 JDK 17 安装路径

示例（路径需要按你的机器环境调整）：

```xml
<toolchain>
    <type>jdk</type>
    <provides>
        <version>11</version>
    </provides>
    <configuration>
        <jdkHome>/usr/lib/jvm/java-11-openjdk</jdkHome>
    </configuration>
</toolchain>
<toolchain>
    <type>jdk</type>
    <provides>
        <version>17</version>
    </provides>
    <configuration>
        <jdkHome>/usr/lib/jvm/java-17-openjdk</jdkHome>
    </configuration>
</toolchain>
```

CI 在执行 Maven 之前也必须提供 JDK 11、JDK 17 以及生成好的 `toolchains.xml`。

### APT 诊断

MongoPlus 注解处理器默认保持静默。如果你需要排查为什么 `@AutoMapper`
或生成的字段元数据被跳过、或为什么被生成，可以开启详细的 APT 日志：

```bash
mvn test -Dmaven.compiler.compilerArgs=-Amongoplus.apt.verbose=true
```

这个选项仅用于排查注解处理器行为。正常的本地构建和 CI 不应设置它。

## 示例模块

examples 被有意放在默认根验证路径之外。
它们属于依赖 Testcontainers 的集成风格示例模块，更适合在具备 Docker 环境的机器上运行。

[docs/example-modules.md](docs/example-modules.md) 中包含 examples reactor profile、模块矩阵、
环境前提以及推荐验证命令。

## 更多文档

### 使用指南

- [docs/example-modules.md](docs/example-modules.md) - 示例 reactor 用法、模块覆盖范围与验证流程
- [docs/plugin-interceptors.md](docs/plugin-interceptors.md) - 拦截器 SPI、支持的上下文类型与注册方式
- [docs/custom-mapper-extension.md](docs/custom-mapper-extension.md) - 自定义 mapper 方法、registry 扩展与 Spring 注册流程

### 核心模块设计

- [docs/engine-design.md](docs/engine-design.md) - engine 门面、query service 边界与可执行查询链路
- [docs/dsl-design.md](docs/dsl-design.md) - 条件 DSL、AST 模型以及编译交接边界
- [docs/wrapper-design.md](docs/wrapper-design.md) - wrapper 状态模型、Lambda 支持与查询 / 更新语义
- [docs/mapper-design.md](docs/mapper-design.md) - mapper 契约分层与基于代理的运行时分发
- [docs/executor-design.md](docs/executor-design.md) - compiler、template executor、批量执行与拦截器装饰链

## License

本项目基于 Apache License 2.0 发布。详见 [LICENSE](LICENSE)。
