# Mapper Design

## Overview

The mapper module defines the contract-oriented CRUD surface of MongoPlus. It intentionally keeps
the API as interfaces so that mapper usage stays declarative while runtime dispatch is supplied by
the Spring Boot integration layer.

In other words:

- `mongo-plus-mapper` defines what a mapper can do
- `mongo-plus-spring` defines how mapper interfaces become executable proxies

## Contract Layers

| Type | Responsibility |
|------|----------------|
| `Mapper<T>` | Marker root for mapper families |
| `SelectMapper<T>` | Query, count, page, and existence methods |
| `InsertMapper<T>` | Insert operations |
| `UpdateMapper<T>` | Update operations |
| `DeleteMapper<T>` | Delete operations |
| `BatchMapper<T>` | Batch insert/update/delete operations |
| `AggregateMapper<T>` | Aggregation-oriented entry points |
| `MongoMapper<T>` | Unified facade that extends all mapper contracts |

`MongoMapper<T>` also adds a few convenience aliases such as `list(...)`, `one(...)`, and
`count(...)`.

## Why The Design Is Split

The split contract model helps in two ways:

- it keeps each capability family readable and easier to evolve
- it allows the top-level `MongoMapper<T>` to act as the default "full capability" contract

This is especially useful for framework-generated or annotation-only mapper scenarios where callers
still want a single interface to depend on.

## Runtime Dispatch Model

Mapper interfaces are not self-executing. Runtime behavior is supplied by the Spring integration
layer:

1. mapper interfaces are discovered through `@MongoMapperScan` or property-based scanner
   configuration
2. `MapperFactoryBean` creates the mapper bean
3. `MapperProxyFactory` builds a JDK dynamic proxy
4. `MapperProxy` resolves a `MapperMethod` from `MethodHandlerRegistry`
5. the resolved handler delegates to `ExecutorGetter` and executor implementations

This design keeps the mapper contract module free of Spring and execution details while still
allowing rich runtime behavior.

## Default Methods And Custom Methods

Two special cases matter:

- default interface methods are invoked directly and do not go through the registry
- custom non-default mapper methods are resolved through `MethodHandlerRegistry`

That means MongoPlus supports both framework-provided CRUD contracts and user-defined extension
methods in the same mapper interface.

## Interceptor Integration

When mapper dispatch runs through `MapperProxy`, it can also pass through the interceptor registry.
The mapper invocation is wrapped as `MapperInvocationContext`, which gives interceptors access to:

- entity class
- mapper method
- command name
- raw arguments

This is how mapper-level tracing or policy checks can be layered on top of the contract model.

## Design Boundaries

The mapper module intentionally does not:

- scan packages
- create Spring beans
- compile wrappers
- access `MongoTemplate`

Those concerns are implemented in `mongo-plus-spring` and `mongo-plus-executor`.

## Related Files

- [README.md](../README.md)
- [docs/plugin-interceptors.md](plugin-interceptors.md)
- [MongoMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/MongoMapper.java)
- [SelectMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/SelectMapper.java)
- [InsertMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/InsertMapper.java)
- [UpdateMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/UpdateMapper.java)
- [DeleteMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/DeleteMapper.java)
- [BatchMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/BatchMapper.java)
- [AggregateMapper.java](../mongo-plus-mapper/src/main/java/io/github/photowey/mongoplus/mapper/AggregateMapper.java)
- [MapperFactoryBean.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/bean/MapperFactoryBean.java)
- [MapperProxy.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/main/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/MapperProxy.java)
