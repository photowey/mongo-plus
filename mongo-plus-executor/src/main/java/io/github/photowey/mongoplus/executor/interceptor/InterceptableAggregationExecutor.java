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

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.plugin.context.AggregationExecutionContext;
import io.github.photowey.mongoplus.plugin.enums.AggregationExecutionOperation;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.InvocationTarget;

/**
 * InterceptableAggregationExecutor - Decorates an {@link AggregationExecutor} with execution interceptor support.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
public class InterceptableAggregationExecutor implements AggregationExecutor {

    private final AggregationExecutor delegate;
    private final InterceptorRegistry interceptorRegistry;

    /**
     * Creates a decorator around the supplied delegate.
     *
     * @param delegate            the wrapped aggregation executor
     * @param interceptorRegistry the interceptor registry used for execution
     */
    public InterceptableAggregationExecutor(
        AggregationExecutor delegate,
        InterceptorRegistry interceptorRegistry
    ) {
        this.delegate = delegate;
        this.interceptorRegistry = Objects.nonNull(interceptorRegistry)
            ? interceptorRegistry
            : InterceptorRegistry.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
        return this.invoke(
            AggregationExecutionContext.builder()
                .operation(AggregationExecutionOperation.EXECUTE)
                .pipeline(pipeline)
                .inputType(inputType)
                .outputType(outputType)
                .build(),
            () -> this.delegate.execute(pipeline, inputType, outputType)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType) {
        return this.invoke(
            AggregationExecutionContext.builder()
                .operation(AggregationExecutionOperation.EXECUTE_BY_COLLECTION)
                .pipeline(pipeline)
                .collectionName(collectionName)
                .outputType(outputType)
                .build(),
            () -> this.delegate.execute(pipeline, collectionName, outputType)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
        return this.invoke(
            AggregationExecutionContext.builder()
                .operation(AggregationExecutionOperation.EXECUTE_RAW)
                .pipeline(pipeline)
                .inputType(inputType)
                .outputType(outputType)
                .build(),
            () -> this.delegate.executeRaw(pipeline, inputType, outputType)
        );
    }

    @SuppressWarnings("unchecked")
    private <R> R invoke(AggregationExecutionContext context, InvocationTarget target) {
        try {
            return (R) this.interceptorRegistry.invoke(context, target);
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to execute aggregation interceptor chain", e);
        }
    }
}
