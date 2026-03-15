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
package io.github.photowey.mongoplus.plugin.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.plugin.context.InvocationContext;
import io.github.photowey.mongoplus.plugin.context.QueryExecutionContext;
import io.github.photowey.mongoplus.plugin.enums.QueryExecutionOperation;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

/**
 * InterceptorRegistryTest - Tests ordered and selective execution behavior for {@link InterceptorRegistry}.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("InterceptorRegistryTest")
class InterceptorRegistryTest {

    @Test
    @DisplayName("Given ordered interceptors, when invoking the registry, then interceptors run in ascending order")
    @Story("Ordered interceptors run in ascending order")
    void givenOrderedInterceptors_whenInvokingRegistry_thenInterceptorsRunInAscendingOrder() throws Throwable {
        // Given
        List<String> calls = new ArrayList<>();
        QueryExecutionContext context = QueryExecutionContext.builder()
            .operation(QueryExecutionOperation.SELECT_LIST)
            .build();
        MongoPlusInterceptor first = new OrderedInterceptor(20, calls, "second");
        MongoPlusInterceptor second = new OrderedInterceptor(10, calls, "first");
        InterceptorRegistry registry = new InterceptorRegistry(List.of(first, second));

        // When
        Object result = registry.invoke(context, () -> {
            calls.add("target");
            return "done";
        });

        // Then
        Assertions.assertEquals("done", result);
        Assertions.assertEquals(List.of("first", "second", "target"), calls);
    }

    @Test
    @DisplayName("Given selective interceptors, when invoking the registry, then unsupported interceptors are skipped")
    @Story("Unsupported interceptors are skipped")
    void givenSelectiveInterceptors_whenInvokingRegistry_thenUnsupportedInterceptorsAreSkipped() throws Throwable {
        // Given
        AtomicInteger counter = new AtomicInteger();
        QueryExecutionContext context = QueryExecutionContext.builder()
            .operation(QueryExecutionOperation.SELECT_ONE)
            .build();
        MongoPlusInterceptor supported = new MongoPlusInterceptor() {
            @Override
            public boolean supports(InvocationContext invocationContext) {
                return true;
            }

            @Override
            public Object intercept(Invocation invocation) throws Throwable {
                counter.incrementAndGet();
                return invocation.proceed();
            }
        };
        MongoPlusInterceptor unsupported = new MongoPlusInterceptor() {
            @Override
            public boolean supports(InvocationContext invocationContext) {
                return false;
            }

            @Override
            public Object intercept(Invocation invocation) {
                counter.addAndGet(100);
                return "unexpected";
            }
        };
        InterceptorRegistry registry = new InterceptorRegistry(List.of(supported, unsupported));

        // When
        Object result = registry.invoke(context, () -> "done");

        // Then
        Assertions.assertEquals("done", result);
        Assertions.assertEquals(1, counter.get());
    }

    @Test
    @DisplayName("Given a short-circuit interceptor, when invoking the registry, then the target is not executed")
    @Story("Short-circuit interceptor skips target execution")
    void givenShortCircuitInterceptor_whenInvokingRegistry_thenTargetIsNotExecuted() throws Throwable {
        // Given
        AtomicInteger targetCalls = new AtomicInteger();
        QueryExecutionContext context = QueryExecutionContext.builder()
            .operation(QueryExecutionOperation.SELECT_COUNT)
            .build();
        MongoPlusInterceptor shortCircuit = invocation -> 99L;
        InterceptorRegistry registry = new InterceptorRegistry(List.of(shortCircuit));

        // When
        Object result = registry.invoke(context, () -> {
            targetCalls.incrementAndGet();
            return 1L;
        });

        // Then
        Assertions.assertEquals(99L, result);
        Assertions.assertEquals(0, targetCalls.get());
    }

    private static final class OrderedInterceptor implements MongoPlusInterceptor {

        private final int order;
        private final List<String> calls;
        private final String label;

        private OrderedInterceptor(int order, List<String> calls, String label) {
            this.order = order;
            this.calls = calls;
            this.label = label;
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public Object intercept(Invocation invocation) throws Throwable {
            this.calls.add(this.label);
            return invocation.proceed();
        }
    }
}
