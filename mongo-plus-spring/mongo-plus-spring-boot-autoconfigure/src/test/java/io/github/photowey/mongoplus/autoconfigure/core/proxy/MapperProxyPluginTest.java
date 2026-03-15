/*
 * Copyright (c) 2026-present The MongoPlus Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.photowey.mongoplus.autoconfigure.core.proxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.autoconfigure.core.enums.Command;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.plugin.context.MapperInvocationContext;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

/**
 * MapperProxyPluginTest - Tests mapper invocation interception behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MapperProxyPluginTest")
class MapperProxyPluginTest {

    @Test
    @DisplayName("Given mapper proxy interceptor, when invoking custom mapper method, then mapper context is exposed")
    @Story("Mapper proxy interceptor exposes mapper context")
    void givenMapperProxyInterceptor_whenInvokingCustomMapperMethod_thenMapperContextIsExposed()
        throws Exception {
        // Given
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Method method = CustomMapper.class.getMethod("findByStatus", String.class);
        registry.register(method, Command.SELECT, (getter, entityClass, args) -> "ok:" + args[0]);
        AtomicInteger interceptorCalls = new AtomicInteger();
        MongoPlusInterceptor interceptor = invocation -> {
            MapperInvocationContext context = (MapperInvocationContext) invocation.getContext();
            Assertions.assertEquals(method, context.getMapperMethod());
            Assertions.assertEquals("SELECT", context.getCommandName());
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };
        MapperProxy<Object> mapperProxy = new MapperProxy<>(
            Object.class,
            new EmptyExecutorGetter(),
            registry,
            new InterceptorRegistry(List.of(interceptor))
        );
        CustomMapper mapper = (CustomMapper) mapperProxy.createProxy(CustomMapper.class);

        // When
        Object result = mapper.findByStatus("active");

        // Then
        Assertions.assertEquals("ok:active", result);
        Assertions.assertEquals(1, interceptorCalls.get());
    }

    @Test
    @DisplayName("Given mapper short-circuit interceptor, when invoking custom mapper method, then handler is skipped")
    @Story("Short-circuit interceptor skips handler")
    void givenMapperShortCircuitInterceptor_whenInvokingCustomMapperMethod_thenHandlerIsSkipped() throws Exception {
        // Given
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        Method method = CustomMapper.class.getMethod("findByStatus", String.class);
        AtomicInteger handlerCalls = new AtomicInteger();
        registry.register(method, Command.SELECT, (getter, entityClass, args) -> {
            handlerCalls.incrementAndGet();
            return "delegate";
        });
        MongoPlusInterceptor interceptor = invocation -> "intercepted";
        MapperProxy<Object> mapperProxy = new MapperProxy<>(
            Object.class,
            new EmptyExecutorGetter(),
            registry,
            new InterceptorRegistry(List.of(interceptor))
        );
        CustomMapper mapper = (CustomMapper) mapperProxy.createProxy(CustomMapper.class);

        // When
        Object result = mapper.findByStatus("active");

        // Then
        Assertions.assertEquals("intercepted", result);
        Assertions.assertEquals(0, handlerCalls.get());
    }

    interface CustomMapper {

        Object findByStatus(String status);
    }

    private static final class EmptyExecutorGetter implements ExecutorGetter {

        @Override
        public QueryExecutor queryExecutor() {
            return null;
        }

        @Override
        public AggregationExecutor aggregationExecutor() {
            return null;
        }
    }
}
