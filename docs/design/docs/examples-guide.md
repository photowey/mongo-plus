# Example Modules

The `mongo-plus-examples` reactor validates starter wiring, mapper scanning, wrappers, APT, and
plugin execution in real Spring Boot applications.

## Module Matrix

| Module | Starter | JDK requirement | Purpose |
| --- | --- | --- | --- |
| `mongo-plus-examples-spring-boot-2x` | `mongo-plus-spring-boot-starter` | Java 11 | Validate the Boot 2.x starter |
| `mongo-plus-examples-spring-boot-3x` | `mongo-plus-spring-boot3-starter` | Java 17 | Validate the Boot 3.x starter |

## Why Example Modules Are Not In The Default Reactor

The default `mvn test` path in the root project only covers the framework core modules so that
daily feedback stays fast and stable.

The example modules depend on:

- Docker / Testcontainers
- A fuller Spring Boot application context
- Dual validation paths for Boot 2.x and Boot 3.x

That is why they live behind the `examples` profile instead of the default verification path.

## Runtime Prerequisites

Before running the examples, confirm that:

- Docker is available.
- The local JDK satisfies the target module requirement.
- Maven Toolchains is configured with both JDK 11 and JDK 17 when the Boot 3 starter is involved.

Recommended pre-checks:

```bash
docker ps
java -version
mvn -q -N toolchains:display-discovered-jdk-toolchains
```

## Common Commands

### Run All Example Modules

```bash
mvn -Pexamples test
```

### Run Only The Boot 2.x Example

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-2x test
```

### Run Only The Boot 3.x Example

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

### Generate An Allure Report Including Examples

```bash
make allure PROFILE=examples
```

## What The Examples Cover

The current example reactor focuses on:

- starter auto-configuration
- dual mapper scanning through `@MongoMapperScan` and configured `base-packages`
- CRUD, paging, update, delete, batch, and aggregation usage
- `@AutoMapper` and annotation-processor output
- custom method registration through `MethodHandlerRegistryCustomizer`
- `MongoPlusInterceptor` execution chains

## When You Should Run The Example Modules

At minimum, run `-Pexamples` when you change:

- `mongo-plus-spring` auto-configuration
- mapper proxy or method-registration logic
- interceptor SPI or invocation-context models
- APT behavior or generated-code logic

## Relationship To Everyday Development

A practical verification split is:

1. Run `mvn test` for normal day-to-day changes.
2. Run `mvn -Pexamples test` when the change touches starters, proxies, or example paths.

This keeps local feedback fast while still preserving integration coverage when it matters.
