# Mapper Interfaces And Scanning

`MongoMapper<T>` is the declarative CRUD entry in Mongo Plus. It groups query, insert, update,
delete, paging, batch, and aggregation-related capability behind one contract, making it suitable
as a Repository / Mapper abstraction.

## Minimal Definition

```java
import io.github.photowey.mongoplus.mapper.MongoMapper;

public interface UserMapper extends MongoMapper<UserDocument> {
}
```

This definition does not require a hand-written implementation. Spring Boot auto-configuration
creates the runtime proxy.

## Two Scanning Modes

### Option 1: `@MongoMapperScan`

```java
@SpringBootApplication
@MongoMapperScan(basePackages = "com.example.user.mapper")
public class DemoApplication {
}
```

This is the most direct option when the primary business mappers live under one stable package.

### Option 2: Configuration Property `base-packages`

```yaml
spring:
  data:
    mongodb:
      mongoplus:
        mapper:
          base-packages: com.example.user.mapper,com.example.audit.mapper
```

This is useful for mappers that live outside the main scan package. The example applications use
this path to validate the `configmapper` and `annotated` packages.

## Common Calls

```java
UserDocument entity = userMapper.selectById(1L);
long total = userMapper.selectCount(Wrappers.lambdaQuery(UserDocument.class));
boolean ok = userMapper.updateById(entity);
BatchResult batchResult = userMapper.insertBatch(users);
```

## Custom Mapper Methods

Mongo Plus supports custom method handlers through `MethodHandlerRegistryCustomizer`.

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

Then declare the same method in the mapper interface:

```java
@MongoMapper
public interface CustomMethodMapper {

    String findByStatus(String status);
}
```

This is a good way to extend mapper behavior for project-specific commands without hard-coding them
into the framework core.

## AutoMapper And Generated Code

If `mongo-plus-apt` is enabled, you can use `@AutoMapper` on an entity:

```java
@AutoMapper
@Document(collection = "generated_users")
public class GeneratedUserDocument {
}
```

The example modules already verify this path through `GeneratedAutoMapperTest`.

## When To Prefer Mapper

Mapper is usually the best fit when:

- The project wants a MyBatis-Plus-style Repository layer.
- CRUD, paging, and batch operations are the dominant path.
- Complex filter composition should stay in wrappers, while execution stays behind one stable
  contract.

## Continue Reading

- If you need interceptors and execution hooks, read
  [Plugins And Extension Points](plugin-guide.md).
- If you need runnable starter validation, read [Example Modules](examples-guide.md).
