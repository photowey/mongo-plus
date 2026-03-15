# Wrapper Design

## Overview

The wrapper module is the fluent state-building layer of MongoPlus. It translates user intent into
two synchronized representations:

- flat runtime state used for sort, projection, pagination, and updates
- an AST used by query compilation

This makes the wrapper module the bridge between the DSL model and the executor/compiler layers.

## Wrapper Family

| Type | Purpose |
|------|---------|
| `AbstractWrapper<T>` | Shared state holder for query and update wrappers |
| `QueryWrapper<T>` | String-field query wrapper |
| `LambdaQueryWrapper<T>` | Lambda-safe query wrapper |
| `UpdateWrapper<T>` | String-field update wrapper with `set` / `inc` state |
| `LambdaUpdateWrapper<T>` | Lambda-safe update wrapper |
| `Wrappers` | Factory entry for creating bound or unbound wrappers |

## What A Wrapper Stores

`AbstractWrapper<T>` maintains:

- `entityClass`
- condition list
- sort list
- projection list
- `Long limit`
- `Long skip`
- `RootNode astRoot`
- active `LogicalNode currentLogical`

`AbstractWrapper<T>` explicitly implements both `ConditionDSL` and `QueryDSL`, which makes
predicate building and query-shaping responsibilities visible at the type level instead of leaving
projection, sort, and pagination as implicit helper methods only.

`UpdateWrapper<T>` adds:

- `setValues`
- `incValues`

This split lets the same wrapper support both compiler-oriented condition modeling and MongoDB
update semantics.

## How Conditions Are Recorded

When a condition is added, the wrapper updates both:

1. the flat `Condition` list
2. the AST under the current logical node

That dual-write behavior is what keeps wrapper-based APIs compatible with both legacy condition
consumers and AST-based compilers.

## Query Variants

### `QueryWrapper<T>`

`QueryWrapper<T>` is the raw-field variant. It is useful when field names are known as strings or
resolved from metadata before the call site.

### `LambdaQueryWrapper<T>`

`LambdaQueryWrapper<T>` resolves Java method references through `LambdaUtils.resolve(...)` and then
reuses the same underlying behavior as `AbstractWrapper<T>`.

This keeps lambda and raw-string wrappers aligned in operator semantics.

## Update Variants

### `UpdateWrapper<T>`

`UpdateWrapper<T>` extends `AbstractWrapper<T>` and adds update assignments:

- `set(field, value)`
- `setNull(field)`
- `inc(field, value)`

Condition building still follows the same `ConditionDSL` rules, which means update wrappers can
carry both the filter predicate and the update payload.

### `LambdaUpdateWrapper<T>`

`LambdaUpdateWrapper<T>` adds lambda-safe `set`, `setNull`, and `inc` methods while reusing the
same update payload maps as `UpdateWrapper<T>`.

It also overrides `paginate(long, long)` so fluent return types stay lambda-specific when pagination
configuration is chained with other lambda-oriented methods.

## Nested Logical Blocks

Wrappers support nested `and(...)` and `or(...)` blocks. A nested block creates another wrapper,
collects the nested state inside that wrapper, and then appends the nested logical structure back to
the parent wrapper's AST and flat condition model.

This design allows nested queries to be composed without exposing AST internals to application code.

## Clone Behavior

Concrete wrappers override `clone()` so callers can duplicate fluent state without sharing mutable
condition, sort, projection, or update collections. This is important for scenarios where a base
wrapper is reused across multiple query variants.

## Factory Entry

`Wrappers` is the standard construction entry point. It supports:

- unbound wrappers
- wrappers bound to an entity class
- wrappers inferred from an entity instance

Using `Wrappers` is the preferred construction path because it keeps entity binding explicit and
consistent.

## Design Boundaries

The wrapper layer intentionally does not:

- execute MongoDB operations
- scan mapper interfaces
- know about `MongoTemplate`
- own the final `Query` / `Update` / `Aggregation` translation contracts

Those responsibilities belong to the query and executor layers.

## Related Files

- [README.md](../README.md)
- [docs/dsl-design.md](dsl-design.md)
- [docs/executor-design.md](executor-design.md)
- [AbstractWrapper.java](../mongo-plus-wrapper/src/main/java/io/github/photowey/mongoplus/wrapper/AbstractWrapper.java)
- [QueryWrapper.java](../mongo-plus-wrapper/src/main/java/io/github/photowey/mongoplus/wrapper/QueryWrapper.java)
- [LambdaQueryWrapper.java](../mongo-plus-wrapper/src/main/java/io/github/photowey/mongoplus/wrapper/LambdaQueryWrapper.java)
- [UpdateWrapper.java](../mongo-plus-wrapper/src/main/java/io/github/photowey/mongoplus/wrapper/UpdateWrapper.java)
- [LambdaUpdateWrapper.java](../mongo-plus-wrapper/src/main/java/io/github/photowey/mongoplus/wrapper/LambdaUpdateWrapper.java)
- [Wrappers.java](../mongo-plus-wrapper/src/main/java/io/github/photowey/mongoplus/wrapper/core/util/Wrappers.java)
