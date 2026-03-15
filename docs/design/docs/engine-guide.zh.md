# Engine 查询入口

Engine 层适合那些不希望在业务服务里直接操作 Mapper，而是希望通过统一查询服务入口组织代码的团队。

Engine 不只是条件门面。当前实现里，`EngineQuery<T>` 还显式继承了独立的 `QueryDSL`
契约，用来承载投影、排序和分页配置能力。

## 调用链

Mongo Plus 的 Engine 入口围绕这条链路展开：

```text
MongoEngine
  -> QueryService
     -> EngineQuery / LambdaEngineQuery
        -> QueryWrapper / LambdaQueryWrapper
           -> QueryExecutor
```

你可以把它理解为“面向业务服务的查询门面”，而不是一个新的执行器。

## Lambda 风格查询

```java
List<UserDocument> users = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getName, "photowey")
    .gte(UserDocument::getAge, 18)
    .orderByDesc(UserDocument::getId)
    .list();
```

这种写法适合业务层直接使用方法引用，不需要显式传字段名字符串。

## 元数据风格查询

如果你的项目已经启用了字段常量生成，可以使用元数据模式：

```java
UserDocument user = mongoEngine
    .queryService()
    .createQuery(UserDocument.class)
    .eq(UserDocumentColumns.USER_NAME, "photowey")
    .one();
```

适合把字段访问统一收敛到编译期生成的常量上。

## QueryDSL 整形能力

因为 `EngineQuery<T>` 继承了 `QueryDSL<T, EngineQuery<T>>`，所以 Engine 入口也可以先做
查询整形，再执行：

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

这里有一个必须明确的语义区分：

- `paginate(long current, long size)` 只负责配置 wrapper 状态
- `page()` / `page(long)` / `page(long, long)` 才是真正执行并返回 `Page<T>`

之所以拆成两个名字，就是为了避免同一个 `page(...)` 既表示“配置分页”又表示“执行分页”。

## 常见终止操作

Engine 查询最终还是回到执行器层，因此你可以直接在查询链尾部调用：

- `one()`
- `list()`
- `count()`
- `exists()`
- `page()`
- `page(long)`
- `page(long, long)`

示例：

```java
boolean exists = mongoEngine
    .queryService()
    .createLambdaQuery(UserDocument.class)
    .eq(UserDocument::getEmail, "demo@example.com")
    .exists();
```

## 嵌套条件

Engine 层对 `and(...)` / `or(...)` 做了包装，内部仍然复用 Wrapper 语义：

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

## 什么时候选 Engine

优先选择 Engine 的典型场景：

- 希望业务代码统一从查询服务入口进入
- 需要把查询构造和实际执行界限表现得更明显
- 团队不想在 Service 层直接暴露 `MongoMapper`

如果你更习惯 CRUD 接口和分页接口，直接用 Mapper 往往更短。

## 与 Mapper 的关系

Engine 不是 Mapper 的替代品，而是另一种上层入口。

| 场景 | 更适合的入口 |
| --- | --- |
| 显式查询服务 | Engine |
| CRUD / 分页 / 批量操作 | Mapper |
| 灵活拼装查询条件 | Wrapper |

## 分页参数类型说明

Mongo Plus 现在把 Engine 侧的分页参数统一为 `long`，以便和 `Page<T>` 的模型保持一致，
同时避免在 fluent API 边界上提前收窄类型。

- 配置分页：`paginate(long, long)`
- 执行分页：`page(long)` 与 `page(long, long)`

## 继续阅读

- 需要直接操作查询和更新条件：看 [Wrapper、分页与聚合](wrapper-guide.md)
- 需要声明式 Mapper：看 [Mapper 接口与扫描](mapper-guide.md)
