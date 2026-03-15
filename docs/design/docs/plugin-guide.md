# Plugins And Extension Points

Mongo Plus exposes its execution interceptor SPI through the `mongo-plus-plugin` module. It is
designed for cross-cutting extension around mapper, query, and aggregation execution flows.

## Core Interface

The extension model centers on `MongoPlusInterceptor`. Spring Boot starter auto-configuration
collects all interceptor beans from the application context and orders them by `getOrder()`.

## Supported Context Types

| Context | Layer | Observable data |
| --- | --- | --- |
| `MapperInvocationContext` | Mapper dynamic-proxy calls | Mapper method, command type, raw arguments |
| `QueryExecutionContext` | Query / Wrapper execution | Entity type, operation type, wrapper, paging arguments |
| `AggregationExecutionContext` | Aggregation execution | Pipeline, input type, output type, collection name |

## Registering An Interceptor

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
                return invocation.proceed();
            }
        };
    }
}
```

## Execution Semantics

- Lower `getOrder()` values run earlier.
- Interceptors whose `supports(context)` returns `false` are skipped.
- An interceptor can short-circuit the chain by not calling `invocation.proceed()`.
- Real mapper / query / aggregation execution happens only after the matched chain is exhausted.

## Good Fit Scenarios

- Trace query execution order and call flow.
- Audit important write operations.
- Add lightweight authorization or feature-flag checks.
- Enrich diagnostics and metrics pipelines.

## Extension Points Beyond Interceptors

Besides interceptors, the current project exposes two common extension surfaces:

### 1. `MethodHandlerRegistryCustomizer`

Used to register custom mapper method handlers in the proxy layer.

### 2. `mongo-plus-apt`

Used to generate field constants or AutoMapper-related artifacts, reducing string-based field names
and boilerplate interfaces.

## Example Validation

The repository already includes end-to-end examples:

- Boot 2.x: `PluginTracingInterceptorExampleTest`
- Boot 3.x: `PluginTracingInterceptorExampleTest`

To validate them directly:

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

## Continue Reading

- For the full example-module story, read [Example Modules](examples-guide.md).
- For build and documentation maintenance commands, read
  [Development, Verification, And Docs Maintenance](development-guide.md).
