# Custom Mapper Extension Guide

## Overview

MongoPlus supports custom mapper methods on top of the built-in CRUD contract. This extension path
is intended for developers who want to:

- add domain-specific methods to a mapper interface
- bind those methods to custom execution logic
- register custom methods through Spring Boot auto-configuration
- keep custom methods inside the same mapper proxy model as built-in methods

The runtime extension chain is:

```text
Custom mapper interface method
  -> AbstractMethod / MapperMethod
     -> MethodHandlerRegistry
        -> MapperProxy
           -> MethodHandler.execute(...)
```

## Core Types

| Type | Responsibility |
|------|----------------|
| `MethodHandler` | Low-level execution callback for one mapper method |
| `MapperMethod` | Metadata object that binds method name / signature, command type, and handler |
| `MethodHandlerRegistry` | Registry that stores built-in and custom mapper methods |
| `AbstractMethod` | MyBatis-Plus-style injectable method abstraction |
| `MethodHandlerRegistryCustomizer` | Spring Boot callback for customizing the registry |
| `MapperProxy` | Runtime dispatch point that resolves mapper methods from the registry |

## How The Registry Works

`MethodHandlerRegistry` has two storage layers:

- method-name templates for methods registered before an actual Java `Method` object is available
- runtime-bound mapper methods keyed by full method signature

At runtime, `MapperProxy` asks the registry for the current reflected method. If a name-based
template exists, the registry binds it to the reflected method signature and caches the bound
result.

This means:

- built-in methods and custom methods share the same dispatch model
- custom methods can first be registered by name and later be resolved by exact Java signature
- overloaded methods can be cached per reflected signature after binding

## Extension Option 1: Register A Handler Directly

If you already have the Java `Method` or only need a small customization, you can register directly
on `MethodHandlerRegistry`.

By method name:

```java
registry.register(
    "findByStatus",
    Command.SELECT,
    (getter, entityClass, args) -> "ok:" + args[0]
);
```

By reflected method:

```java
Method method = CustomMethodMapper.class.getMethod("findByStatus", String.class);
registry.register(
    method,
    Command.SELECT,
    (getter, entityClass, args) -> "ok:" + args[0]
);
```

Use this path when:

- the customization is local and small
- you do not need a reusable injector abstraction
- you want a direct registry mutation in tests or bootstrap code

## Extension Option 2: Create An `AbstractMethod`

For reusable custom methods, prefer `AbstractMethod`. It encapsulates three things:

- mapper method name
- command type
- handler factory

Example:

```java
public class FindByStatusMethod extends AbstractMethod {

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
}
```

Register it with:

```java
registry.addInjector(new FindByStatusMethod());
```

Use this path when:

- you want a reusable, named extension unit
- you want your custom method to look like a first-class mapper feature
- you may contribute multiple custom methods as a package or module

## Extension Option 3: Register Through Spring Boot

In normal applications, the preferred registration point is `MethodHandlerRegistryCustomizer`.

Example:

```java
@Bean
public MethodHandlerRegistryCustomizer findByStatusCustomizer() {
    return registry -> registry.addInjector(new FindByStatusMethod());
}
```

This is the cleanest path for Spring Boot applications because:

- the registry bean is created centrally in auto-configuration
- all customizers are applied during bean creation
- custom methods become available to all mapper proxies created afterward

MongoPlus auto-configuration currently creates the registry like this:

```java
@Bean
@ConditionalOnMissingBean(MethodHandlerRegistry.class)
public MethodHandlerRegistry methodHandlerRegistry(Optional<List<MethodHandlerRegistryCustomizer>> customizers) {
    MethodHandlerRegistry registry = new MethodHandlerRegistry();
    customizers.orElse(Collections.emptyList())
        .forEach(it -> it.customize(registry));
    return registry;
}
```

## Mapper Interface Example

Declare the custom method on your mapper interface:

```java
public interface CustomMethodMapper extends MongoMapper<UserDocument> {

    String findByStatus(String status);
}
```

As long as the method name and signature resolve through the registry, `MapperProxy` can dispatch it
like a built-in mapper method.

## Runtime Dispatch Details

`MapperProxy` resolves a `MapperMethod` from `MethodHandlerRegistry` and then executes the
registered `MethodHandler`:

```java
MapperMethod mapperMethod = this.registry.getMapperMethod(method);
return mapperMethod.handler().execute(this.getter, this.entityClass, actualArguments);
```

This means the custom handler has access to:

- `ExecutorGetter`
- resolved entity class
- raw mapper arguments

You can therefore delegate to existing query or aggregation executors inside your custom method if
needed.

## Example Project Reference

The repository already contains a runnable example of this extension mechanism:

- configuration:
  [TestMongoPlusCustomizerConfig.java](../mongo-plus-examples/mongo-plus-examples-spring-boot-3x/src/test/java/io/github/photowey/mongoplus/examples/spring/boot/b3x/config/TestMongoPlusCustomizerConfig.java)
- integration test:
  [CustomMethodMapperTest.java](../mongo-plus-examples/mongo-plus-examples-spring-boot-3x/src/test/java/io/github/photowey/mongoplus/examples/spring/boot/b3x/mapper/CustomMethodMapperTest.java)

The example registers a custom `findByStatus` method through `MethodHandlerRegistryCustomizer` and
verifies that the mapper proxy dispatches to the custom handler. The mapper interface itself is
declared inside the example test source set and exercised through the integration test above.

## When To Use Which Approach

Use direct `register(...)` when:

- you are writing a test
- you want a one-off handler
- you already have the reflected `Method`

Use `AbstractMethod` when:

- you want a reusable extension unit
- you expect to package multiple custom mapper methods together
- you want a cleaner abstraction boundary for custom features

Use `MethodHandlerRegistryCustomizer` when:

- you are integrating with Spring Boot
- you want registry customization to happen during application bootstrap
- you want your custom mapper methods available framework-wide

## Related Files

- [README.md](../README.md)
- [docs/mapper-design.md](mapper-design.md)
- [MethodHandlerRegistry.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/handler/MethodHandlerRegistry.java)
- [MethodHandlerRegistryCustomizer.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/handler/MethodHandlerRegistryCustomizer.java)
- [AbstractMethod.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/method/AbstractMethod.java)
- [MapperMethod.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/method/MapperMethod.java)
- [MapperProxy.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/MapperProxy.java)
- [AbstractMongoPlusConfiguration.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/config/AbstractMongoPlusConfiguration.java)
