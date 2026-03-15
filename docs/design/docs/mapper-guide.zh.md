# Mapper 接口与扫描

`MongoMapper<T>` 是 Mongo Plus 的声明式 CRUD 入口。它把查询、插入、更新、删除、分页、
批量与聚合相关能力组织成一个统一接口，适合直接作为 Repository / Mapper 层契约。

## 最小定义

```java
import io.github.photowey.mongoplus.mapper.MongoMapper;

public interface UserMapper extends MongoMapper<UserDocument> {
}
```

这条定义本身不需要写任何实现，运行时由 Spring Boot 自动配置生成代理对象。

## 两种扫描方式

### 方式一：`@MongoMapperScan`

```java
@SpringBootApplication
@MongoMapperScan(basePackages = "com.example.user.mapper")
public class DemoApplication {
}
```

这是最直观的方式，适合主业务 Mapper 都落在固定包下时使用。

### 方式二：配置项 `base-packages`

```yaml
spring:
  data:
    mongodb:
      mongoplus:
        mapper:
          base-packages: com.example.user.mapper,com.example.audit.mapper
```

这条路径适合补充主扫描包之外的 Mapper，示例工程中的 `configmapper` 和 `annotated`
包就是通过这个入口验证的。

## 常见调用

```java
UserDocument entity = userMapper.selectById(1L);
long total = userMapper.selectCount(Wrappers.lambdaQuery(UserDocument.class));
boolean ok = userMapper.updateById(entity);
BatchResult batchResult = userMapper.insertBatch(users);
```

## 自定义 Mapper 方法

Mongo Plus 支持通过 `MethodHandlerRegistryCustomizer` 注册自定义方法处理器。

```java
@Bean
public MethodHandlerRegistryCustomizer findByStatusCustomizer() {
    return registry -> registry.addInjector(new AbstractMethod() {
        @Override
        public String getMethodName() {
            return "findByStatus";
        }

        @Override
        protected Command getCommand() {
            return Command.SELECT;
        }

        @Override
        protected MethodHandler createHandler() {
            return (getter, entityClass, args) -> {
                if (args != null && args.length > 0 && args[0] != null) {
                    return "ok:" + args[0];
                }
                return "ok:null";
            };
        }
    });
}
```

然后在 Mapper 接口里声明同名方法即可：

```java
@MongoMapper
public interface CustomMethodMapper {

    String findByStatus(String status);
}
```

这条能力适合为框架补充业务自定义命令，而不是硬编码到公共模块里。

## AutoMapper 与生成代码

如果启用了 `mongo-plus-apt`，可以在实体上使用 `@AutoMapper`：

```java
@AutoMapper
@Document(collection = "generated_users")
public class GeneratedUserDocument {
}
```

示例模块中已经用 `GeneratedAutoMapperTest` 验证了这条链路。

## 什么时候优先选 Mapper

优先选 Mapper 的场景：

- 项目需要 MyBatis-Plus 风格 Repository 层
- CRUD、分页和批量操作是主路径
- 希望把复杂条件留给 Wrapper，但执行入口仍然保持统一

## 继续阅读

- 需要插件与拦截器：看 [插件与扩展点](plugin-guide.md)
- 需要示例模块验证路径：看 [示例模块](examples-guide.md)
