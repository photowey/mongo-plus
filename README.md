English | [简体中文](README.zh-CN.md)

# `mongo-plus`

`mongo-plus` is a MongoDB data access framework for Spring Boot that combines an engine-style query
entry with a MyBatis-Plus-inspired wrapper and mapper model.
It lets application code read as `MongoEngine -> QueryService -> Query`, while retaining fluent
wrappers, lambda-safe field references, aggregation helpers, mapper-style interfaces, and Spring
Boot starter integration on top of Spring Data MongoDB.

## Highlights

- Engine-style runtime entry: `MongoEngine -> QueryService -> EngineQuery`
- Fluent query and update wrappers: `QueryWrapper`, `LambdaQueryWrapper`, `UpdateWrapper`, `LambdaUpdateWrapper`
- AST-based query compilation and aggregation pipeline modeling
- Mapper-style CRUD, pagination, batch operations, and aggregation entry points
- Metadata-driven id handling and update execution
- Execution interceptor SPI for mapper, query, and aggregation invocation chains
- Spring Boot integration with auto-configured engine, mapper scanning, and dynamic proxy dispatch
- Compile-time APT support for generated field/index constants
- Spring Boot 2.x starter by default, Spring Boot 3 starter on Java 17+

## Modules

| Module                   | Responsibility                                                                             |
|--------------------------|--------------------------------------------------------------------------------------------|
| `mongo-plus-annotation`  | Framework annotations                                                                      |
| `mongo-plus-bom`         | BOM for aligning published MongoPlus artifact versions                                     |
| `mongo-plus-core`        | Core utilities, metadata, pagination, geo model, id generation                             |
| `mongo-plus-dsl`         | DSL abstractions and AST model                                                             |
| `mongo-plus-wrapper`     | Query/update wrapper implementations                                                       |
| `mongo-plus-query`       | Query builder and AST compiler support                                                     |
| `mongo-plus-aggregation` | Aggregation stages and wrapper DSL                                                         |
| `mongo-plus-optimizer`   | Aggregation/query optimization rules                                                       |
| `mongo-plus-executor`    | `MongoTemplate`-based execution layer plus `MongoEngine` / `QueryService` integration APIs |
| `mongo-plus-mapper`      | Mapper contracts                                                                           |
| `mongo-plus-plugin`      | Interceptor SPI, invocation context model, and execution extension points                  |
| `mongo-plus-spring`      | Spring Boot auto-configuration and starters                                                |
| `mongo-plus-apt`         | Annotation processor for generated constants                                               |
| `mongo-plus-examples`    | Example applications and integration-style sample tests                                    |

### Reactor layout

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
└── mongo-plus-examples            # enabled with -Pexamples
    ├── mongo-plus-examples-spring-boot-2x
    └── mongo-plus-examples-spring-boot-3x
```

## Installation

### Spring Boot 2.x

```xml

<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-spring-boot-starter</artifactId>
    <version>${mongo-plus.version}</version>
</dependency>
```

### Spring Boot 3.x

Use the Boot 3 starter when building with Java 17 or newer:

```xml

<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-spring-boot3-starter</artifactId>
    <version>${mongo-plus.version}</version>
</dependency>
```

## Quick Start

### Entity

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

### Engine-first service usage

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

### Mapper-based service usage

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

## Current API Surface

### Engine query entry

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getUserName, "photowey")
    .gte(UserDocument::getAge, 18)
    .orderByDesc(UserDocument::getId)
    .list();
```

### Engine metadata query entry

```java
UserDocument user = mongoEngine
    .queryService()
    .createQuery(UserDocument.class)
    .eq(UserDocumentColumns.USER_NAME, "photowey")
    .one();
```

### Plugin interceptors

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

Spring Boot starter auto-configuration collects all `MongoPlusInterceptor` beans, sorts them by
`getOrder()`, and applies them to mapper dispatch, query execution, and aggregation execution.
See [docs/plugin-interceptors.md](docs/plugin-interceptors.md) for more details.

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

### Pagination

```java
Page<UserDocument> page = new Page<>(1L, 10L, true);
Page<UserDocument> result = userMapper.selectPage(
    page,
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .orderByDesc(UserDocument::getId)
);
```

### Aggregation

```java
AggregationWrapper<UserDocument> wrapper = AggregationWrapper.aggregation(UserDocument.class)
    .match(query -> query.gte(UserDocument::getAge, 18))
    .group(UserDocument::getAge)
    .count("total");
```

### Batch operations

```java
BatchResult result = userMapper.insertBatch(users);
```

## Build and Verification

### Default local verification

The default root verification path is:

```bash
mvn test
```

This path is intended to stay portable and does not require the examples reactor.

### Maven Toolchains (required for boot3-starter)

The `mongo-plus-spring-boot3-starter` module targets JDK 17 and is built via Maven Toolchains.
The rest of the project uses JDK 11. You must configure `~/.m2/toolchains.xml` with both JDKs:

1. Copy the template: `cp toolchains.xml.template ~/.m2/toolchains.xml`
2. Edit `~/.m2/toolchains.xml` and set `<jdkHome>` to your JDK 11 and JDK 17 installation paths.

Example (paths depend on your system):

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

CI must provide both JDK 11 and 17 and a generated `toolchains.xml` before running Maven.

### APT diagnostics

MongoPlus annotation processors stay quiet by default. When you need to inspect why
`@AutoMapper` or generated field metadata was skipped or produced, enable verbose APT logging:

```bash
mvn test -Dmaven.compiler.compilerArgs=-Amongoplus.apt.verbose=true
```

Use this option only for debugging processor behavior. Normal local builds and CI should leave it unset.

## Examples

The examples are intentionally kept outside the default root verification path.
They are integration-style sample modules that rely on Testcontainers and are best run in environments with Docker
available.

See [docs/example-modules.md](docs/example-modules.md) for the example reactor profile, module matrix,
environment prerequisites, and recommended verification commands.

## Additional Docs

### Usage guides

- [docs/example-modules.md](docs/example-modules.md) - example reactor usage, module coverage, and verification flow
- [docs/plugin-interceptors.md](docs/plugin-interceptors.md) - interceptor SPI, supported contexts, and registration model
- [docs/custom-mapper-extension.md](docs/custom-mapper-extension.md) - custom mapper methods, registry extension, and Spring registration flow

### Core module design

- [docs/engine-design.md](docs/engine-design.md) - engine facade, query-service boundary, and executable query flow
- [docs/dsl-design.md](docs/dsl-design.md) - condition DSL, AST model, and compilation hand-off
- [docs/wrapper-design.md](docs/wrapper-design.md) - wrapper state model, lambda support, and update/query semantics
- [docs/mapper-design.md](docs/mapper-design.md) - mapper contract layering and proxy-based runtime dispatch
- [docs/executor-design.md](docs/executor-design.md) - compiler, template executor, batch, and interceptor-backed execution

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
