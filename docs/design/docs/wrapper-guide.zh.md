# Wrapper、分页与聚合

Wrapper 是 Mongo Plus 最接近 MyBatis-Plus 使用体验的一层。它负责记录查询条件、排序、投影、
分页以及更新语义，并把这些状态交给执行器编译和执行。

在当前代码里，Wrapper 不再只是“顺带拥有查询整形方法”的实现细节。`AbstractWrapper<T>`
已经显式实现了 `ConditionDSL` 与 `QueryDSL` 两层契约。

## QueryWrapper 与 LambdaQueryWrapper

字符串字段名模式：

```java
List<UserDocument> users = userMapper.selectList(
    Wrappers.<UserDocument>query(UserDocument.class)
        .eq("name", "photowey")
        .gte("age", 18)
        .orderByDesc("id")
        .limit(10L)
);
```

Lambda 安全模式：

```java
List<UserDocument> users = userMapper.selectList(
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .eq(UserDocument::getName, "photowey")
        .gte(UserDocument::getAge, 18)
        .orderByDesc(UserDocument::getId)
        .limit(10L)
);
```

实际项目里更推荐 Lambda 版本，因为字段改名时更安全。

## UpdateWrapper 与 LambdaUpdateWrapper

更新示例：

```java
boolean updated = userMapper.update(
    null,
    Wrappers.<UserDocument>lambdaUpdate(UserDocument.class)
        .eq(UserDocument::getId, 1L)
        .set(UserDocument::getEmail, "next@example.com")
        .inc(UserDocument::getAge, 1)
);
```

`UpdateWrapper` 同时携带过滤条件和更新 payload，因此很适合表达 MongoDB 的
“按条件更新”语义。

## QueryDSL 的职责

当前查询型 Wrapper 的能力已经显式拆成两层：

- `ConditionDSL`
  负责 `eq`、`gt`、`like`、`and`、`or` 这类过滤条件
- `QueryDSL`
  负责投影、排序、`limit(long)`、`skip(long)` 以及 `paginate(long, long)`

这让“条件构造”和“查询整形”之间的边界变得更清晰，而不是继续隐藏在实现细节里。

## 分页

Mongo Plus 提供 `Page<T>` 作为统一分页模型：

```java
Page<UserDocument> page = new Page<>(1L, 10L, true);

Page<UserDocument> result = userMapper.selectPage(
    page,
    Wrappers.<UserDocument>lambdaQuery(UserDocument.class)
        .orderByDesc(UserDocument::getId)
);
```

这条路径适合后台列表页、管理台筛选页和带总数统计的接口。

Wrapper 侧的分页配置现在也统一使用 `long`：

```java
LambdaQueryWrapper<UserDocument> wrapper = Wrappers.lambdaQuery(UserDocument.class)
    .orderByDesc(UserDocument::getId)
    .paginate(2L, 10L);
```

兼容性说明：

- `paginate(long, long)` 是现在的标准整形 API
- `page(long, long)` 在 Wrapper 上仍然保留，作为兼容别名

## 聚合

聚合能力通过 `AggregationWrapper<T>` 暴露：

```java
AggregationWrapper<UserDocument> wrapper = AggregationWrapper.aggregation(UserDocument.class)
    .match(query -> query.gte(UserDocument::getAge, 18))
    .group(UserDocument::getAge)
    .count("total");
```

这适合把 `$match`、`$group`、`$project` 等阶段通过 DSL 方式拼出来，而不是在业务代码里直接写原始管道。

## 批量操作

批量写入和删除可以直接通过 `MongoMapper<T>` 完成：

```java
BatchResult insertResult = userMapper.insertBatch(users);
BatchResult deleteResult = userMapper.deleteBatch(ids);
```

批量结果会集中返回执行统计，适合导入、清理与批量修正场景。

## 何时优先选择 Wrapper

以下场景更适合直接使用 Wrapper：

- Service 层已经基于 Mapper 组织代码
- 需要同时组合过滤条件、排序、分页和更新语义
- 需要在方法间复用查询模板，或通过 `clone()` 派生多个条件分支

## 与 Engine 的边界

- Engine 更像“业务可读性优先”的运行时入口
- Wrapper 更像“查询条件表达能力优先”的状态构建工具

两者最终都复用相同的执行链路，并不会分裂成两套底层实现。

## 继续阅读

- 需要声明式 CRUD 接口：看 [Mapper 接口与扫描](mapper-guide.md)
- 需要插件拦截与扩展：看 [插件与扩展点](plugin-guide.md)
