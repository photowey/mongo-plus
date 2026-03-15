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
package io.github.photowey.mongoplus.executor.interceptor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.plugin.context.AggregationExecutionContext;
import io.github.photowey.mongoplus.plugin.enums.AggregationExecutionOperation;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

/**
 * InterceptableAggregationExecutorTest - Tests aggregation execution interception behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("InterceptableAggregationExecutorTest")
class InterceptableAggregationExecutorTest {

    @Test
    @DisplayName("Given aggregation interception, when executing, then interceptor receives aggregation context")
    @Story("Interceptor receives aggregation context on execution")
    void givenAggregationInterception_whenExecuting_thenInterceptorReceivesAggregationContext() {
        // Given
        RecordingAggregationExecutor delegate = new RecordingAggregationExecutor();
        AtomicInteger interceptorCalls = new AtomicInteger();
        MongoPlusInterceptor interceptor = invocation -> {
            AggregationExecutionContext context = (AggregationExecutionContext) invocation.getContext();
            Assertions.assertEquals(AggregationExecutionOperation.EXECUTE, context.getOperation());
            Assertions.assertSame(delegate.pipeline, context.getPipeline());
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };
        InterceptableAggregationExecutor executor = new InterceptableAggregationExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );

        // When
        List<String> result = executor.execute(delegate.pipeline, Object.class, String.class);

        // Then
        Assertions.assertSame(delegate.executeResult, result);
        Assertions.assertEquals(1, interceptorCalls.get());
        Assertions.assertEquals(1, delegate.executeCalls.get());
    }

    @Test
    @DisplayName("Given short-circuit aggregation interceptor, when executing raw, then delegate is skipped")
    @Story("Short-circuit aggregation interceptor skips delegate")
    void givenShortCircuitAggregationInterceptor_whenExecutingRaw_thenDelegateIsSkipped() {
        // Given
        RecordingAggregationExecutor delegate = new RecordingAggregationExecutor();
        MongoPlusInterceptor interceptor = invocation -> List.of("short-circuit");
        InterceptableAggregationExecutor executor = new InterceptableAggregationExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );

        // When
        List<String> result = executor.executeRaw(delegate.pipeline, Object.class, String.class);

        // Then
        Assertions.assertEquals(List.of("short-circuit"), result);
        Assertions.assertEquals(0, delegate.executeRawCalls.get());
    }

    @Test
    @DisplayName(
        "Given collection execution null registries and checked throwables, when invoking the "
            + "executor, then delegates correctly or wraps the checked failure"
    )
    @Story("Delegate or wrap checked failures with null registries")
    void givenCollectionExecutionNullRegistriesAndCheckedThrowables_whenInvokingTheExecutor_thenDelegatesOrWrapsCheckedFailures() {
        // Given
        RecordingAggregationExecutor delegate = new RecordingAggregationExecutor();
        AtomicInteger interceptorCalls = new AtomicInteger();
        MongoPlusInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };
        InterceptableAggregationExecutor defaultExecutor = new InterceptableAggregationExecutor(delegate, null);
        InterceptableAggregationExecutor collectionExecutor = new InterceptableAggregationExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );
        InterceptableAggregationExecutor failingExecutor = new InterceptableAggregationExecutor(
            delegate,
            new InterceptorRegistry(
                List.of(
                    invocation -> {
                        throw new Exception("aggregation-boom");
                    }
                )
            )
        );

        // When
        List<String> byCollection = collectionExecutor.execute(delegate.pipeline, "users", String.class);
        List<String> byDefault = defaultExecutor.execute(delegate.pipeline, Object.class, String.class);
        IllegalStateException error = Assertions.assertThrows(
            IllegalStateException.class,
            () -> failingExecutor.execute(delegate.pipeline, "users", String.class)
        );

        // Then
        Assertions.assertEquals(delegate.executeResult, byCollection);
        Assertions.assertEquals(delegate.executeResult, byDefault);
        Assertions.assertEquals(1, interceptorCalls.get());
        Assertions.assertEquals(1, delegate.executeByCollectionCalls.get());
        Assertions.assertEquals(1, delegate.executeCalls.get());
        Assertions.assertEquals("Failed to execute aggregation interceptor chain", error.getMessage());
        Assertions.assertEquals("aggregation-boom", error.getCause().getMessage());
    }

    private static final class RecordingAggregationExecutor implements AggregationExecutor {

        private final AtomicInteger executeCalls = new AtomicInteger();
        private final AtomicInteger executeRawCalls = new AtomicInteger();
        private final AtomicInteger executeByCollectionCalls = new AtomicInteger();
        private final Pipeline pipeline = Pipeline.builder().build();
        private final List<String> executeResult = List.of("done");

        @Override
        public <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
            this.executeCalls.incrementAndGet();
            return (List<R>) this.executeResult;
        }

        @Override
        public <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType) {
            this.executeByCollectionCalls.incrementAndGet();
            return (List<R>) this.executeResult;
        }

        @Override
        public <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
            this.executeRawCalls.incrementAndGet();
            return (List<R>) this.executeResult;
        }
    }
}
