# Mongo Plus Guide

<div class="hero">
    <div class="hero__visual">
        <img src="assets/logo.svg" alt="Mongo Plus logo" width="240">
    </div>
    <div class="hero__body">
        <p class="hero__eyebrow">Spring Boot · MongoDB · Engine-first Access</p>
        <p class="hero__title">A focused guide for mapper-driven and engine-driven MongoDB access.</p>
        <p class="hero__lead">
            Mongo Plus is a MongoDB data access framework for Spring Boot. It combines an engine-style
            query entry, a MyBatis-Plus-inspired Wrapper / Mapper model, aggregation support, plugin
            interceptors, and Spring Boot starters to reduce MongoDB data access boilerplate.
        </p>
        <div class="hero__chips">
            <span>MongoEngine</span>
            <span>Wrapper DSL</span>
            <span>MongoMapper</span>
            <span>APT</span>
            <span>Interceptors</span>
        </div>
    </div>
</div>

This directory is no longer a copied design draft from another project. It is now the official
manual for the current `mongo-plus` repository.

## What Mongo Plus Solves

- It structures query flows as `MongoEngine -> QueryService -> Query`.
- It unifies filtering, sorting, paging, and update semantics through
  `QueryWrapper`, `LambdaQueryWrapper`, and `UpdateWrapper`.
- It exposes CRUD, paging, batch, and aggregation entry points through `MongoMapper<T>`.
- It provides a single interceptor SPI for mapper, query, and aggregation execution chains.
- It supports generated field constants and AutoMapper-related output through `mongo-plus-apt`.

## Entry Styles

| Entry | Best for | Representative types |
| --- | --- | --- |
| Engine | Teams that prefer an explicit service-style query entry | `MongoEngine`, `QueryService` |
| Wrapper + Mapper | Teams that want MyBatis-Plus-like CRUD and paging | `MongoMapper<T>`, `Wrappers` |
| Aggregation | Teams that want to compose aggregation pipelines with a DSL | `AggregationWrapper<T>` |
| Plugin | Teams that need auditing, tracing, throttling, or short-circuit hooks | `MongoPlusInterceptor` |

## Recommended Reading Order

1. Start with [Requirements And Dependencies](getting-started.md) to wire starters, JDKs, and
   basic configuration.
2. Continue with [Quick Start](quick-start.md) to connect entities, mappers, services, and
   configuration.
3. Choose [Engine Query Entry](engine-guide.md) or
   [Wrapper, Paging, And Aggregation](wrapper-guide.md) based on your preferred programming model.
4. If you need proxy extension, APT, or custom methods, continue with
   [Mapper Interfaces And Scanning](mapper-guide.md) and
   [Plugins And Extension Points](plugin-guide.md).
5. If you need starter-level validation or runnable integration samples, read
   [Example Modules](examples-guide.md).

## Module Map

| Module | Purpose |
| --- | --- |
| `mongo-plus-core` | Utilities, paging, metadata, id generation, and base models |
| `mongo-plus-wrapper` | Query / update wrappers and lambda wrappers |
| `mongo-plus-query` | Query compilation and AST-related support |
| `mongo-plus-aggregation` | Aggregation DSL and stage models |
| `mongo-plus-executor` | `MongoTemplate`-based execution and the `MongoEngine` entry |
| `mongo-plus-mapper` | Mapper contracts |
| `mongo-plus-plugin` | Interceptor SPI and invocation context types |
| `mongo-plus-spring` | Starters, auto-configuration, mapper scanning, and proxies |
| `mongo-plus-apt` | Generated field constants and AutoMapper-related annotation processing |
| `mongo-plus-examples` | Boot 2.x / 3.x samples and integration validation |

## Short Conclusion

!!! tip
    If you only want the fastest path to validate the framework:

    1. Add the starter from [Requirements And Dependencies](getting-started.md).
    2. Create the entity and mapper from [Quick Start](quick-start.md).
    3. Run `mvn test` from the repository root.
    4. Run `mvn -Pexamples test` when you need integration coverage.
