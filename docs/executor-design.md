# Executor Design

## Overview

The executor layer is the runtime core that turns MongoPlus abstractions into executable Spring Data
MongoDB operations. It sits below wrappers and engine queries, and above `MongoTemplate`.

This layer is responsible for:

- compiling wrappers into `Query`
- translating update wrapper state into `Update`
- compiling aggregation pipelines into Spring Data `Aggregation`
- executing CRUD, pagination, batch, and aggregation calls
- decorating execution with interceptor-aware contexts

## Primary Runtime Interfaces

| Type | Responsibility |
|------|----------------|
| `SimpleExecutor` | Simple entity-oriented operations such as insert/update/delete by id |
| `QueryExecutor` | Wrapper-based query, count, exists, page, and update execution |
| `BatchExecutor` | Batch insert/update/delete execution |
| `AggregationExecutor` | Aggregation pipeline execution |
| `QueryCompiler` | Wrapper-to-`Query` compiler contract |
| `PipelineCompiler` | Pipeline-to-`Aggregation` compiler contract |

## Default Implementations

| Type | Responsibility |
|------|----------------|
| `DefaultMongoTemplateExecutor` | Main `MongoTemplate`-backed query/simple/batch executor |
| `DefaultMongoTemplateAggregationExecutor` | Main aggregation executor |
| `MongoQueryCompiler` | Query compiler implementation that delegates to `QueryBuilder` |
| `MongoPipelineCompiler` | Aggregation compiler implementation for stage pipelines |
| `InterceptableQueryExecutor` | Decorator that wraps query execution with interceptor support |
| `InterceptableAggregationExecutor` | Decorator that wraps aggregation execution with interceptor support |

## Query Execution Flow

```text
Wrapper / EngineQuery / Mapper method
  -> QueryExecutor
     -> QueryCompiler
        -> QueryBuilder + AstCompiler
           -> Query / Criteria
              -> MongoTemplate
```

Key points:

- `MongoQueryCompiler` is the main wrapper-to-query entry.
- `QueryBuilder` applies AST compilation, sort, projection, `skip`, and `limit`.
- `DefaultMongoTemplateExecutor` handles terminal `MongoTemplate` calls such as `find`, `findOne`,
  `count`, `exists`, `updateMulti`, and `bulkOps`.

## Update Execution Model

Update execution is deliberately split into:

- predicate state from `UpdateWrapper<T>`
- assignment state from `setValues` and `incValues`

`DefaultMongoTemplateExecutor` converts wrapper update state into Spring Data `Update` objects and
uses entity metadata when building update-by-id operations from entity instances.

## Aggregation Execution Flow

```text
Pipeline
  -> QueryOptimizer
     -> PipelineCompiler
        -> Aggregation
           -> MongoTemplate.aggregate(...)
```

`DefaultMongoTemplateAggregationExecutor` optimizes the pipeline before compilation for normal
execution paths, while `executeRaw(...)` skips optimization and runs the original pipeline as-is.

## Batch Execution

Batch operations are implemented through Spring Data `BulkOperations`. The default executor keeps
batch handling in the same runtime module so that:

- single and batch operations share metadata rules
- bulk write acknowledgement and error reporting are normalized through `BatchResult`
- mapper, engine, and direct executor callers all use the same runtime implementation

## Interceptor Decoration

The executor layer is also where execution interception becomes concrete:

- `InterceptableQueryExecutor` wraps each query/simple/batch operation in `QueryExecutionContext`
- `InterceptableAggregationExecutor` wraps aggregation calls in `AggregationExecutionContext`

This means interceptors can observe execution without changing wrapper, engine, or mapper APIs.

## Design Boundaries

The executor layer intentionally does not:

- expose user-facing fluent query syntax
- own AST node definitions
- scan mapper packages
- define Spring Boot starter activation rules

Those concerns belong to the wrapper, DSL, mapper, and spring modules.

## Related Files

- [README.md](../README.md)
- [docs/dsl-design.md](dsl-design.md)
- [docs/wrapper-design.md](wrapper-design.md)
- [docs/plugin-interceptors.md](plugin-interceptors.md)
- [QueryCompiler.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/compiler/QueryCompiler.java)
- [MongoQueryCompiler.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/compiler/impl/MongoQueryCompiler.java)
- [PipelineCompiler.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/compiler/PipelineCompiler.java)
- [MongoPipelineCompiler.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/compiler/impl/MongoPipelineCompiler.java)
- [DefaultMongoTemplateExecutor.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/template/impl/DefaultMongoTemplateExecutor.java)
- [DefaultMongoTemplateAggregationExecutor.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/template/impl/DefaultMongoTemplateAggregationExecutor.java)
- [InterceptableQueryExecutor.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/interceptor/InterceptableQueryExecutor.java)
- [InterceptableAggregationExecutor.java](../mongo-plus-executor/src/main/java/io/github/photowey/mongoplus/executor/interceptor/InterceptableAggregationExecutor.java)
