# Requirements And Dependencies

This page describes the minimum prerequisites for integrating Mongo Plus into a Spring Boot
application.

## Runtime Requirements

| Component | Requirement |
| --- | --- |
| JDK | Java 11 for the default modules; Java 17+ when using the Boot 3 starter |
| Maven | Maven 3.9+ is recommended |
| MongoDB | A reachable MongoDB instance, or Testcontainers MongoDB for tests |
| Spring Boot | `mongo-plus-spring-boot-starter` for Boot 2.x, `mongo-plus-spring-boot3-starter` for Boot 3.x |

## Dependency Setup

### Option 1: Add The Starter Directly

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

### Option 2: Align Versions Through The BOM

If the project imports multiple Mongo Plus modules, use `mongo-plus-bom` to align versions:

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

Then keep module coordinates only and stop repeating the version on each dependency.

## Basic Configuration

The smallest working configuration only needs Spring Data MongoDB to connect successfully:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mongo_plus
```

If you also want to declare mapper scanning through configuration, add:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mongo_plus
      mongoplus:
        mapper:
          base-packages: com.example.user.mapper,com.example.order.mapper
```

This can coexist with `@MongoMapperScan` and is useful when some mappers live outside the primary
application scan package.

## Root Verification Path

The recommended local verification order in the repository is:

```bash
mvn test
```

This covers the default reactor, does not require the example modules, and does not depend on
Docker.

If you also want the example modules included:

```bash
mvn -Pexamples test
```

## Toolchains

`mongo-plus-spring-boot3-starter` targets JDK 17, while the rest of the main modules target
JDK 11. If you want to build the full project including the Boot 3 starter, configure Maven
Toolchains first.

```bash
cp toolchains.xml.template ~/.m2/toolchains.xml
```

Then replace the `<jdkHome>` values in `~/.m2/toolchains.xml` with the real local JDK paths.

## Next Step

- Continue with [Quick Start](quick-start.md) to connect the entity, mapper, and service.
- If you prefer an explicit service-oriented query entry, read [Engine Query Entry](engine-guide.md).
- If you need CRUD, paging, or aggregation, read
  [Wrapper, Paging, And Aggregation](wrapper-guide.md).
