# 快速开始

本页给出一个最小可运行的接入示例，覆盖实体、Mapper、Service 与可选的 APT 配置。

## 1. 定义实体

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

`@MongoId` 不是必须，但当你希望由 Mongo Plus 负责主键生成策略时，它是最直接的声明方式。

## 2. 定义 Mapper

```java
import io.github.photowey.mongoplus.mapper.MongoMapper;

public interface UserMapper extends MongoMapper<UserDocument> {
}
```

如果项目更偏好声明式 CRUD，这就是最常用入口。

## 3. 开启 Mapper 扫描

推荐在 Spring Boot 启动类上声明：

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

## 4. 在业务层使用 Mapper

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

## 5. 在业务层使用 Engine

如果你的团队更偏好显式服务边界，而不是直接依赖 Mapper，也可以使用 `MongoEngine`：

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

## 6. 可选：启用 APT

如果你希望使用生成的字段常量或 `@AutoMapper` 相关输出，需要引入 `mongo-plus-apt`
并配置注解处理器。

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

开启后，你可以在实体上使用 `@AutoMapper`：

```java
import io.github.photowey.mongoplus.annotation.AutoMapper;

@AutoMapper
@Document(collection = "generated_users")
public class GeneratedUserDocument {
}
```

## 7. 第一轮验证

完成上述配置后，建议至少执行一次：

```bash
mvn test
```

如果你正在验证 Starter 自动配置或代理扫描链路，再执行：

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

## 接下来读什么

- 需要更细的服务链式查询说明：看 [Engine 查询入口](engine-guide.md)
- 需要 CRUD、分页、聚合与批量说明：看 [Wrapper、分页与聚合](wrapper-guide.md)
- 需要自定义 Mapper 方法或拦截器：看 [插件与扩展点](plugin-guide.md)
