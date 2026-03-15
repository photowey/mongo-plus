# Engine Query Entry

The Engine layer is intended for teams that do not want to construct wrappers directly in the
service layer, and instead prefer a single query-service style entry point.

Engine is not just a condition facade. In the current implementation, `EngineQuery<T>` also exposes
the standalone `QueryDSL` query-shaping contract for projection, sorting, and pagination
configuration.

## Call Chain

Mongo Plus structures the Engine entry around this flow:

```text
MongoEngine
  -> QueryService
     -> EngineQuery / LambdaEngineQuery
        -> QueryWrapper / LambdaQueryWrapper
           -> QueryExecutor
```

You can think of it as a service-oriented query facade, not as a separate executor.

## Lambda-Style Queries

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getName, "photowey")
    .gte(UserDocument::getAge, 18)
    .orderByDesc(UserDocument::getId)
    .list();
```

This style works well when the service layer prefers method references instead of raw field-name
strings.

## Metadata-Style Queries

If the project already enables generated field constants, you can use the metadata-oriented path:

```java
UserDocument user = mongoEngine
    .queryService()
    .createQuery(UserDocument.class)
    .eq(UserDocumentColumns.USER_NAME, "photowey")
    .one();
```

This is useful when field access should be consolidated through compile-time generated constants.

## QueryDSL Shaping Operations

Because `EngineQuery<T>` extends `QueryDSL<T, EngineQuery<T>>`, the engine entry can shape the
query before execution:

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getName, "photowey")
    .orderByDesc(UserDocument::getId)
    .limit(10L)
    .paginate(2L, 10L)
    .list();
```

Important distinction:

- `paginate(long current, long size)` configures wrapper state only
- `page()` / `page(long)` / `page(long, long)` executes and returns `Page<T>`

This split exists to avoid overloading the same method name with two different meanings.

## Common Terminal Operations

Engine queries still delegate to the executor layer, so you can finish the query with:

- `one()`
- `list()`
- `count()`
- `exists()`
- `page()`
- `page(long)`
- `page(long, long)`

Example:

```java
boolean exists = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getEmail, "demo@example.com")
    .exists();
```

## Nested Conditions

The Engine layer wraps `and(...)` and `or(...)`, while still reusing Wrapper semantics internally:

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .and(nested -> nested
        .eq(UserDocument::getName, "photowey")
        .gte(UserDocument::getAge, 18)
    )
    .or(nested -> nested.eq(UserDocument::getEmail, "fallback@example.com"))
    .list();
```

## When To Prefer Engine

Engine is usually the better option when:

- You want all business queries to start from a single query-service entry point.
- You want the separation between query construction and execution to be explicit.
- You do not want to expose `MongoMapper` directly in the service layer.

If your project is heavily CRUD- and paging-oriented, direct mapper usage is often shorter.

## Relationship To Mapper

Engine does not replace Mapper. It is another upper-level entry style.

| Scenario | Better entry |
| --- | --- |
| Explicit query service | Engine |
| CRUD / paging / batch operations | Mapper |
| Flexible query-condition composition | Wrapper |

## Pagination Type Notes

Mongo Plus now treats engine-side pagination parameters as `long` to stay aligned with the
`Page<T>` model and avoid silent narrowing at the fluent API boundary.

- Query-shaping pagination: `paginate(long, long)`
- Terminal pagination: `page(long)` and `page(long, long)`

## Continue Reading

- If you want to work directly with query and update conditions, read
  [Wrapper, Paging, And Aggregation](wrapper-guide.md).
- If you want declarative mapper contracts, read
  [Mapper Interfaces And Scanning](mapper-guide.md).
