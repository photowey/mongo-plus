# 环境与依赖

本页描述把 Mongo Plus 接入到 Spring Boot 应用所需的最小前提。

## 运行前提

| 组件 | 要求 |
| --- | --- |
| JDK | 默认模块使用 Java 11；使用 Boot 3 Starter 时需要 Java 17+ |
| Maven | 建议 Maven 3.9+ |
| MongoDB | 可访问的 MongoDB 实例，或用于测试的 Testcontainers MongoDB |
| Spring Boot | 2.x 使用 `mongo-plus-spring-boot-starter`；3.x 使用 `mongo-plus-spring-boot3-starter` |

## 依赖引入

### 方式一：直接引入 Starter

Spring Boot 2.x:

```xml
<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-spring-boot-starter</artifactId>
    <version>${mongo-plus.version}</version>
</dependency>
```

Spring Boot 3.x:

```xml
<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-spring-boot3-starter</artifactId>
    <version>${mongo-plus.version}</version>
</dependency>
```

### 方式二：使用 BOM 统一版本

如果项目会同时引入多个 Mongo Plus 模块，建议通过 `mongo-plus-bom` 对齐版本：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.photowey</groupId>
            <artifactId>mongo-plus-bom</artifactId>
            <version>${mongo-plus.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后只保留具体模块坐标，不再重复写版本号。

## 基础配置

最小可运行配置只需要让 Spring Data MongoDB 正常连接 MongoDB：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mongo_plus
```

如果你希望通过配置项额外声明 Mapper 扫描包，可以增加：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mongo_plus
      mongoplus:
        mapper:
          base-packages: com.example.user.mapper,com.example.order.mapper
```

这套配置与 `@MongoMapperScan` 可以并存，适合把一部分 Mapper 放在主扫描包之外时使用。

## 根工程验证路径

当前仓库推荐的本地验证顺序如下：

```bash
mvn test
```

这条命令覆盖默认 reactor，不依赖示例模块，也不要求 Docker。

如果你需要把示例模块也纳入验证：

```bash
mvn -Pexamples test
```

## Toolchains 说明

仓库中 `mongo-plus-spring-boot3-starter` 目标 JDK 为 17，其余主体模块默认使用 JDK 11。
如果你要构建包含 Boot 3 Starter 的全量工程，需要提前配置 Maven Toolchains。

```bash
cp toolchains.xml.template ~/.m2/toolchains.xml
```

然后把 `~/.m2/toolchains.xml` 里的 `<jdkHome>` 改成本机真实路径。

## 下一步

- 继续阅读 [快速开始](quick-start.md)，把实体、Mapper 和 Service 连接起来。
- 需要直接面向业务服务入口时，看 [Engine 查询入口](engine-guide.md)。
- 需要 CRUD / 分页 / 聚合时，看 [Wrapper、分页与聚合](wrapper-guide.md)。
