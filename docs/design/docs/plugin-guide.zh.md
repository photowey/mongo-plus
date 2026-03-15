# 插件与扩展点

Mongo Plus 通过 `mongo-plus-plugin` 模块暴露执行拦截器 SPI，用来覆盖查询、聚合和 Mapper
调用链的横切扩展场景。

## 核心接口

插件扩展围绕 `MongoPlusInterceptor` 展开。Spring Boot Starter 会自动收集上下文中的
所有拦截器 Bean，并按照 `getOrder()` 顺序组装执行链。

## 支持的上下文类型

| 上下文 | 适用层 | 可读取的信息 |
| --- | --- | --- |
| `MapperInvocationContext` | Mapper 动态代理调用 | Mapper 方法、命令类型、原始参数 |
| `QueryExecutionContext` | Query / Wrapper 查询执行 | 实体类型、操作类型、Wrapper、分页参数 |
| `AggregationExecutionContext` | 聚合执行 | 管道、输入类型、输出类型、集合名 |

## 注册拦截器

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

## 执行语义

- `getOrder()` 值越小，越先执行
- `supports(context)` 返回 `false` 的拦截器会被跳过
- 如果拦截器不调用 `invocation.proceed()`，可以实现短路逻辑
- 匹配链路耗尽后，才会进入真实的 Mapper / Query / Aggregation 执行器

## 适合的使用场景

- 记录查询链路与调用顺序
- 审计关键写操作
- 接入轻量级鉴权或开关控制
- 为诊断平台补充统计与埋点

## 除拦截器外的扩展点

除了拦截器，当前工程还暴露了两类常见扩展能力：

### 1. `MethodHandlerRegistryCustomizer`

用于给 Mapper 代理注册自定义方法处理器，适合补充业务专用命令。

### 2. `mongo-plus-apt`

用于生成字段常量或 AutoMapper 相关产物，适合减少字符串字段名和样板接口。

## 示例验证

仓库中已经提供了端到端示例：

- Boot 2.x: `PluginTracingInterceptorExampleTest`
- Boot 3.x: `PluginTracingInterceptorExampleTest`

需要验证时直接运行：

```bash
mvn -Pexamples -pl :mongo-plus-examples-spring-boot-3x test
```

## 继续阅读

- 需要完整的示例模块说明：看 [示例模块](examples-guide.md)
- 需要工程构建与文档维护命令：看 [开发、验证与文档维护](development-guide.md)
