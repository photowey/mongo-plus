# Engine Design

## Overview

The engine layer provides the runtime entry that turns MongoPlus usage into an explicit
`MongoEngine -> QueryService -> EngineQuery` chain. Its purpose is not to execute MongoDB calls by
itself, but to expose a stable, service-oriented facade over the lower-level wrapper and executor
layers.

This layer exists for teams that prefer an engine-style application API over directly constructing
wrappers or calling mapper methods.

## Primary Types

| Type | Role |
|------|------|
| `MongoEngine` | Top-level runtime facade |
| `DefaultMongoEngine` | Default implementation that delegates to `QueryService` |
| `QueryService` | Factory boundary for executable query objects |
| `DefaultQueryService` | Default service that creates metadata or lambda query facades |
| `EngineQuery<T>` | Metadata-oriented executable query contract plus `QueryDSL` query shaping |
| `LambdaEngineQuery<T>` | Lambda-oriented executable query contract |
| `AbstractEngineQuery<T, W, S>` | Shared fluent behavior and terminal operation support |
| `DefaultEngineQuery<T>` | Metadata query implementation backed by `QueryWrapper<T>` |
| `DefaultLambdaEngineQuery<T>` | Lambda query implementation backed by `LambdaQueryWrapper<T>` |

## Design Goals

- Keep application code readable through an explicit service boundary.
- Offer both metadata-driven and lambda-driven query styles.
- Reuse wrapper semantics instead of building a second query model.
- Delegate execution to the executor layer rather than binding engine code to `MongoTemplate`.

## End-To-End Flow

```text
MongoEngine
  -> QueryService
     -> EngineQuery / LambdaEngineQuery
        -> QueryWrapper / LambdaQueryWrapper
           -> QueryExecutor
              -> MongoTemplate-backed executor
```

The flow works like this:

1. Spring auto-configuration wires a `QueryExecutor`.
2. `DefaultQueryService` uses that executor to create either `DefaultEngineQuery` or
   `DefaultLambdaEngineQuery`.
3. Fluent condition methods mutate the wrapper directly, while query-shaping methods come from the
   dedicated `QueryDSL` contract and also mutate the same underlying wrapper state.
4. Terminal methods such as `one()`, `list()`, `count()`, and `page()` delegate to `QueryExecutor`.

## Query Styles

### Metadata-Oriented Query

The metadata-oriented path is useful when the caller already has generated field metadata:

```java
UserDocument user = mongoEngine
    .queryService()
    .createQuery(UserDocument.class)
    .eq(UserDocumentColumns.USER_NAME, "photowey")
    .one();
```

### Lambda-Oriented Query

The lambda-oriented path is useful when callers prefer method references:

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .gte(UserDocument::getAge, 18)
    .orderByDesc(UserDocument::getId)
    .list();
```

## Nested Predicate Behavior

`AbstractEngineQuery` delegates nested logical composition to the wrapper layer, but wraps nested
wrappers back into a sibling engine query instance before passing them to the user callback:

```java
engineQuery.and(nested -> nested.eq("status", 1).gt("age", 18));
```

This keeps the engine programming model consistent even inside nested `and(...)` and `or(...)`
blocks.

## QueryDSL Integration

`EngineQuery<T>` now extends the standalone `QueryDSL<T, EngineQuery<T>>` contract. This makes the
following query-shaping capabilities explicit at the interface level:

- `select(...)`
- `exclude(...)`
- `orderByAsc(...)`
- `orderByDesc(...)`
- `orderBy(...)`
- `limit(long)`
- `skip(long)`
- `paginate(long, long)`

The terminal `page(...)` methods remain execution methods on `EngineQuery<T>`. To avoid breaking
the existing runtime behavior of `page(current, size)`, the query-shaping contract uses
`paginate(...)` instead of reusing the same method name.

## Terminal Operations

The engine layer exposes the following terminal categories:

- `one()`
- `list()`
- `count()`
- `exists()`
- `page()`

The engine does not implement these operations directly. It forwards them to `QueryExecutor`,
which means all query compilation, metadata resolution, interception, and actual MongoDB access
remain centralized in the executor layer.

## Design Boundaries

The engine layer intentionally does not:

- scan mapper interfaces
- compile wrappers to `Query`
- talk directly to `MongoTemplate`
- manage aggregation execution

Those concerns belong to the spring, query, and executor modules.

## Related Files

- [README.md](../README.md)
- [docs/executor-design.md](executor-design.md)
- [MongoEngine.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/engine/MongoEngine.java)
- [DefaultMongoEngine.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/engine/DefaultMongoEngine.java)
- [QueryService.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/service/QueryService.java)
- [DefaultQueryService.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/service/DefaultQueryService.java)
- [AbstractEngineQuery.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/query/AbstractEngineQuery.java)
- [DefaultEngineQuery.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/query/DefaultEngineQuery.java)
- [DefaultLambdaEngineQuery.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/integration/query/DefaultLambdaEngineQuery.java)
