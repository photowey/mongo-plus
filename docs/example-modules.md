# Example Modules Guide

## Overview

The `mongo-plus-examples` reactor contains integration-style sample applications for the published
Spring Boot starters. These modules are intentionally kept outside the default root `mvn test`
path because they depend on Docker/Testcontainers and therefore have stricter environment
requirements than the core unit and component test suites.

Use this guide when you need to:

- validate starter auto-configuration in a running Spring Boot application
- verify mapper scanning, wrapper execution, service flows, and plugin interception end-to-end
- run only the Boot 2.x or Boot 3.x sample module for targeted regression checks
- include example modules in a root-level verification or Allure report

## Reactor And Profile Behavior

The root reactor does not include `mongo-plus-examples` by default. The `examples` profile in the
root `pom.xml` adds both the example reactor and the `mongo-plus-report` module:

```xml
<profile>
    <id>examples</id>
    <modules>
        <module>mongo-plus-examples</module>
        <module>mongo-plus-report</module>
    </modules>
</profile>
```

That leads to the following verification paths:

- `mvn test`
  Default fast verification for framework modules only.
- `mvn -Pexamples test`
  Includes the example reactor in the test phase.
- `mvn -Pexamples verify`
  Includes the example reactor and generates the root Allure report when report generation is
  configured.
- `make allure PROFILE=examples`
  Runs a root `verify` build with examples enabled and writes the aggregate Allure report to
  `target/allure-report/index.html`.

## Module Matrix

| Module                               | Starter under test                    | Java requirement | Primary purpose |
|--------------------------------------|---------------------------------------|------------------|-----------------|
| `mongo-plus-examples-spring-boot-2x` | `mongo-plus-spring-boot-starter`      | Java 11          | Validate Boot 2.x starter wiring and integration scenarios |
| `mongo-plus-examples-spring-boot-3x` | `mongo-plus-spring-boot3-starter`     | Java 17          | Validate Boot 3.x starter wiring and integration scenarios |

## Environment Requirements

Before running the example modules, make sure the following prerequisites are satisfied:

- Docker is installed and available to Testcontainers.
- The required JDK is available for the module you want to run.
- When the build includes `mongo-plus-spring-boot3-starter`, Maven Toolchains must be configured
  with both JDK 11 and JDK 17.

Recommended checks:

- `docker ps`
- `java -version`
- `mvn -q -N toolchains:display-discovered-jdk-toolchains`

## Running The Examples

### Run The Full Example Reactor From The Repository Root

```bash
mvn -Pexamples test
```

Use this path when you want to validate both Boot 2.x and Boot 3.x example applications in a
single run.

### Run A Single Example Module From The Repository Root

Spring Boot 2.x:

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-2x test
```

Spring Boot 3.x:

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

Use these paths when you only need one starter path or when the local machine does not satisfy the
other module's Java requirements.

### Run From The `mongo-plus-examples` Reactor Directory

Spring Boot 2.x:

```bash
cd mongo-plus-examples
mvn -pl mongo-plus-examples-spring-boot-2x test
```

Spring Boot 3.x:

```bash
cd mongo-plus-examples
mvn -pl mongo-plus-examples-spring-boot-3x test
```

This mode is useful when you want to stay inside the example reactor and work on one module at a
time.

## What The Examples Cover

The example modules are intended to verify user-facing programming flows rather than every internal
implementation detail. Current example coverage includes:

- starter-driven engine auto-configuration
- mapper scanning and bean registration
- annotation-only mapper usage
- generated mapper wiring through APT output
- custom method registration with `MethodHandlerRegistryCustomizer`
- query, update, delete, pagination, batch, and aggregation flows
- service-level CRUD scenarios
- plugin interceptor registration and tracing behavior

The Boot 2.x and Boot 3.x sample modules are designed to exercise the same supported wrapper and
mapper behavior surface so that the starter variants stay aligned.

## Recommended Verification Workflow

Use the layers below from fastest to most environment-sensitive:

1. `mvn test`
   Best for everyday local development and quick regression feedback.
2. `mvn -Pexamples -pl :mongo-plus-examples-spring-boot-2x test`
   Best when validating the Boot 2.x starter path only.
3. `mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test`
   Best when validating the Boot 3.x starter path only.
4. `mvn -Pexamples verify`
   Best for a full integration-oriented verification run.
5. `make allure PROFILE=examples`
   Best when you want the same integration run plus a root Allure report.

## Related Files

- [README.md](../README.md)
- [docs/plugin-interceptors.md](plugin-interceptors.md)
- [mongo-plus-examples/pom.xml](../mongo-plus-examples/pom.xml)
- [mongo-plus-examples/mongo-plus-examples-spring-boot-2x/pom.xml](../mongo-plus-examples/mongo-plus-examples-spring-boot-2x/pom.xml)
- [mongo-plus-examples/mongo-plus-examples-spring-boot-3x/pom.xml](../mongo-plus-examples/mongo-plus-examples-spring-boot-3x/pom.xml)
