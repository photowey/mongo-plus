# Wrapper, Paging, And Aggregation

Wrapper is the layer in Mongo Plus that feels closest to MyBatis-Plus. It records filters, sort
orders, projections, paging, and update semantics, then hands that state to the compiler and
executor layers.

In the current codebase, Wrapper no longer carries query-shaping behavior as an implicit extension
only. `AbstractWrapper<T>` now explicitly implements both `ConditionDSL` and `QueryDSL`.

## QueryWrapper And LambdaQueryWrapper

String-field mode:

```java
List<UserDocument> users = userMapper.selectList(
    Wrappers.<UserDocument>query(UserDocument.class)
        .eq("name", "photowey")
        .gte("age", 18)
        .orderByDesc("id")
        .limit(10L)
);
```

Lambda-safe mode:

```java
List<UserDocument> users = userMapper.selectList(
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .eq(UserDocument::getName, "photowey")
        .gte(UserDocument::getAge, 18)
        .orderByDesc(UserDocument::getId)
        .limit(10L)
);
```

The Lambda version is usually the better default in real projects because it is safer during field
renames.

## UpdateWrapper And LambdaUpdateWrapper

Update example:

```java
boolean updated = userMapper.update(
    null,
    Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
        .eq(UserDocument::getId, 1L)
        .set(UserDocument::getEmail, "next@example.com")
        .inc(UserDocument::getAge, 1)
);
```

`UpdateWrapper` carries both the filter predicate and the update payload, which maps naturally to
MongoDB's conditional update model.

## QueryDSL Responsibilities

The query-oriented wrapper surface is now explicitly split like this:

- `ConditionDSL`
  filter predicates such as `eq`, `gt`, `like`, `and`, and `or`
- `QueryDSL`
  projection, sorting, `limit(long)`, `skip(long)`, and `paginate(long, long)`

That makes the type boundary visible instead of keeping sorting and paging as implementation-only
helper methods.

## Paging

Mongo Plus uses `Page<T>` as its shared paging model:

```java
Page<UserDocument> page = new Page<>(1L, 10L, true);

Page<UserDocument> result = userMapper.selectPage(
    page,
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .orderByDesc(UserDocument::getId)
);
```

This works well for admin tables, filtered list pages, and APIs that require total counts.

Wrapper-side pagination configuration now uses `long` as well:

```java
LambdaQueryWrapper<UserDocument> wrapper = Wrappers.lambdaQuery(UserDocument.class)
    .orderByDesc(UserDocument::getId)
    .paginate(2L, 10L);
```

Compatibility note:

- `paginate(long, long)` is the canonical query-shaping API
- `page(long, long)` is still kept on wrappers as a compatibility alias

## Aggregation

Aggregation capability is exposed through `AggregationWrapper<T>`:

```java
AggregationWrapper<UserDocument> wrapper = AggregationWrapper.aggregation(UserDocument.class)
    .match(query -> query.gte(UserDocument::getAge, 18))
    .group(UserDocument::getAge)
    .count("total");
```

This lets you compose `$match`, `$group`, `$project`, and similar stages through a DSL instead of
writing raw pipelines directly in business code.

## Batch Operations

Batch insert and delete are available directly through `MongoMapper<T>`:

```java
BatchResult insertResult = userMapper.insertBatch(users);
BatchResult deleteResult = userMapper.deleteBatch(ids);
```

The batch result consolidates execution statistics and is well suited for import, cleanup, and bulk
correction scenarios.

## When To Prefer Wrapper

Use Wrapper directly when:

- The service layer is already organized around mappers.
- You need to combine filter predicates, sorting, paging, and update semantics in one place.
- You need to reuse a query template across methods, or branch it through `clone()`.

## Boundary With Engine

- Engine focuses on business-facing readability and an explicit runtime entry.
- Wrapper focuses on expressive condition construction and mutable query state.

They still reuse the same execution chain underneath and do not split into separate
implementations.

## Continue Reading

- If you need declarative CRUD contracts, read [Mapper Interfaces And Scanning](mapper-guide.md).
- If you need interceptors or extension hooks, read
  [Plugins And Extension Points](plugin-guide.md).
