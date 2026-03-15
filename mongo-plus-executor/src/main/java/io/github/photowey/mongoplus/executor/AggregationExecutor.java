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
package io.github.photowey.mongoplus.executor;

import java.util.List;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;

/**
 * AggregationExecutor - Executes aggregation pipelines.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface AggregationExecutor {

    /**
     * Execute aggregation pipeline with optimization.
     *
     * @param pipeline   the pipeline to execute
     * @param inputType  the input collection type
     * @param outputType the output result type
     * @param <T>        the input type
     * @param <R>        the output type
     * @return the aggregation results
     */
    <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType);

    /**
     * Execute aggregation with collection name.
     *
     * @param pipeline       the pipeline to execute
     * @param collectionName the collection name
     * @param outputType     the output result type
     * @param <R>            the output type
     * @return the aggregation results
     */
    <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType);

    /**
     * Execute aggregation without optimization.
     *
     * @param pipeline   the pipeline to execute
     * @param inputType  the input collection type
     * @param outputType the output result type
     * @param <T>        the input type
     * @param <R>        the output type
     * @return the aggregation results
     */
    <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType);
}
