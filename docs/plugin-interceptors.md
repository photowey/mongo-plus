# Plugin Interceptors Guide

## Overview

`mongo-plus` exposes an execution interceptor SPI through the `mongo-plus-plugin` module. The SPI
is centered on `MongoPlusInterceptor` and is designed for runtime observation, policy enforcement,
and short-circuiting around mapper, query, and aggregation execution flows.

In Spring Boot applications, starter auto-configuration collects all `MongoPlusInterceptor` beans,
sorts them by `getOrder()`, and applies them through a reusable invocation chain.

## Supported Invocation Contexts

The interceptor contract is execution-focused and currently supports three invocation context
categories:

| Context type | Invocation type | Typical metadata |
|--------------|-----------------|------------------|
| `MapperInvocationContext` | `MAPPER` | entity class, mapper method, command name, method arguments |
| `QueryExecutionContext` | `QUERY` | operation, entity class, collection name, wrapper arguments, page arguments |
| `AggregationExecutionContext` | `AGGREGATION` | operation, pipeline, input type, output type, collection name |

These context types let an interceptor inspect what is about to run without depending on a single
execution implementation.

## Registering An Interceptor

Declare a `MongoPlusInterceptor` bean in the Spring application context:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.photowey.mongoplus.plugin.context.InvocationContext;
import io.github.photowey.mongoplus.plugin.context.QueryExecutionContext;
import io.github.photowey.mongoplus.plugin.interceptor.Invocation;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;

@Configuration
public class MongoPlusPluginConfiguration {

    @Bean
    public MongoPlusInterceptor tracingInterceptor() {
        return new MongoPlusInterceptor() {
            @Override
            public int getOrder() {
                return 10;
            }

            @Override
            public boolean supports(InvocationContext context) {
                return context instanceof QueryExecutionContext;
            }

            @Override
            public Object intercept(Invocation invocation) throws Throwable {
                QueryExecutionContext context = (QueryExecutionContext) invocation.getContext();
                // Inspect wrapper, page, or operation metadata here.
                return invocation.proceed();
            }
        };
    }
}
```

## Execution Model

The interceptor registry executes with the following semantics:

- Interceptors are sorted in ascending `getOrder()` value.
- Interceptors whose `supports(context)` returns `false` are skipped for that invocation.
- The chain executes only the matched interceptors for the current context.
- An interceptor may short-circuit execution by returning without calling `invocation.proceed()`.
- When the chain is exhausted, the terminal mapper/query/aggregation target executes.

This makes the SPI suitable for tracing, auditing, metrics, lightweight access checks, and
conditional result substitution.

## Choosing The Right Context

Use the context type that matches the layer you need to observe:

- `MapperInvocationContext`
  Best when you need mapper interface method metadata such as method name or raw arguments.
- `QueryExecutionContext`
  Best when you need to inspect wrapper-based operations, pagination inputs, or query operation
  categories.
- `AggregationExecutionContext`
  Best when you need to inspect or log aggregation pipelines and input/output types.

## Example Use Cases

Common interceptor scenarios include:

- tracing mapper and query execution order
- recording query and aggregation operation types for diagnostics
- blocking or short-circuiting selected operations under feature flags
- enriching audit logs with mapper method names, wrappers, or pagination metadata

## Example Modules And Tests

The example modules include end-to-end interceptor samples that can be used as reference
implementations:

- Boot 2.x configuration:
  [TracingInterceptorExampleConfiguration.java](../mongo-plus-examples/mongo-plus-examples-spring-boot-2x/src/test/java/io/github/photowey/mongoplus/examples/spring/boot/b2x/config/TracingInterceptorExampleConfiguration.java)
- Boot 2.x verification:
  [PluginTracingInterceptorExampleTest.java](../mongo-plus-examples/mongo-plus-examples-spring-boot-2x/src/test/java/io/github/photowey/mongoplus/examples/spring/boot/b2x/plugin/PluginTracingInterceptorExampleTest.java)
- Boot 3.x configuration:
  [TracingInterceptorExampleConfig.java](../mongo-plus-examples/mongo-plus-examples-spring-boot-3x/src/test/java/io/github/photowey/mongoplus/examples/spring/boot/b3x/config/TracingInterceptorExampleConfig.java)
- Boot 3.x verification:
  [PluginTracingInterceptorExampleTest.java](../mongo-plus-examples/mongo-plus-examples-spring-boot-3x/src/test/java/io/github/photowey/mongoplus/examples/spring/boot/b3x/plugin/PluginTracingInterceptorExampleTest.java)

For lower-level execution-chain behavior, see:

- [InterceptorRegistryTest.java](../mongo-plus-plugin/src/test/java/io/github/photowey/mongoplus/plugin/interceptor/InterceptorRegistryTest.java)
- [MapperProxyPluginTest.java](../mongo-plus-spring/mongo-plus-spring-boot-autoconfigure/src/test/java/io/github/photowey/mongoplus/autoconfigure/core/proxy/MapperProxyPluginTest.java)
- [InterceptableQueryExecutorTest.java](../mongo-plus-executor/src/test/java/io/github/photowey/mongoplus/executor/interceptor/InterceptableQueryExecutorTest.java)
- [InterceptableAggregationExecutorTest.java](../mongo-plus-executor/src/test/java/io/github/photowey/mongoplus/executor/interceptor/InterceptableAggregationExecutorTest.java)

## Related Files

- [README.md](../README.md)
- [docs/example-modules.md](example-modules.md)
- [MongoPlusInterceptor.java](../mongo-plus-plugin/src/main/java/io/github/photowey/mongoplus/plugin/interceptor/MongoPlusInterceptor.java)
- [InterceptorRegistry.java](../mongo-plus-plugin/src/main/java/io/github/photowey/mongoplus/plugin/interceptor/InterceptorRegistry.java)
