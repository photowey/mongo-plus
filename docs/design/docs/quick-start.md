# Quick Start

This page provides a minimal runnable example that covers the entity, mapper, service, and
optional APT configuration.

## 1. Define The Entity

```java
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.github.photowey.mongoplus.annotation.MongoId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @MongoId(type = MongoId.IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String email;
    private Integer age;
}
```

`@MongoId` is optional, but it is the most direct way to declare that Mongo Plus should manage the
id generation strategy.

## 2. Define The Mapper

```java
import io.github.photowey.mongoplus.mapper.MongoMapper;

public interface UserMapper extends MongoMapper<UserDocument> {
}
```

This is the most common entry point when the project prefers declarative CRUD.

## 3. Enable Mapper Scanning

The recommended option is to declare it on the Spring Boot application class:

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.photowey.mongoplus.autoconfigure.core.annotation.MongoMapperScan;

@SpringBootApplication
@MongoMapperScan(basePackages = "com.example.user.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## 4. Use The Mapper In The Service Layer

```java
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;

@Service
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

## 5. Use Engine In The Service Layer

If your team prefers an explicit service boundary instead of depending on mappers directly, you can
use `MongoEngine`:

```java
import java.util.List;

import io.github.photowey.mongoplus.executor.integration.engine.MongoEngine;

public class UserQueryService {

    private final MongoEngine mongoEngine;

    public UserQueryService(MongoEngine mongoEngine) {
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

## 6. Optional: Enable APT

If you want generated field constants or AutoMapper-related output, add `mongo-plus-apt` and
configure it as an annotation processor.

```xml
<dependency>
    <groupId>io.github.photowey</groupId>
    <artifactId>mongo-plus-apt</artifactId>
    <scope>provided</scope>
</dependency>
```

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths combine.children="append">
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
            <path>
                <groupId>io.github.photowey</groupId>
                <artifactId>mongo-plus-apt</artifactId>
                <version>${mongo-plus.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

After that, you can use `@AutoMapper` on an entity:

```java
import io.github.photowey.mongoplus.annotation.AutoMapper;

@AutoMapper
@Document(collection = "generated_users")
public class GeneratedUserDocument {
}
```

## 7. First Verification Pass

After wiring the pieces above, run at least:

```bash
mvn test
```

If you are validating starter auto-configuration or the mapper proxy chain, also run:

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

## What To Read Next

- If you want a more detailed service-style query explanation, read
  [Engine Query Entry](engine-guide.md).
- If you need CRUD, paging, aggregation, and batch usage, read
  [Wrapper, Paging, And Aggregation](wrapper-guide.md).
- If you need custom mapper methods or interceptors, read
  [Plugins And Extension Points](plugin-guide.md).
