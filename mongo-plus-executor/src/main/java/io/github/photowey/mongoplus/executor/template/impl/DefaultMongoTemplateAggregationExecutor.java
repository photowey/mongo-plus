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
package io.github.photowey.mongoplus.executor.template.impl;

import java.util.List;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.compiler.PipelineCompiler;
import io.github.photowey.mongoplus.executor.compiler.impl.MongoPipelineCompiler;
import io.github.photowey.mongoplus.executor.template.MongoTemplateAggregationExecutor;
import io.github.photowey.mongoplus.optimizer.QueryOptimizer;

/**
 * DefaultMongoTemplateAggregationExecutor - Executes aggregation pipelines.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class DefaultMongoTemplateAggregationExecutor
    extends AbstractMongoTemplateExecutor implements MongoTemplateAggregationExecutor {

    private final PipelineCompiler pipelineCompiler;
    private final QueryOptimizer optimizer;

    public DefaultMongoTemplateAggregationExecutor(PipelineCompiler pipelineCompiler, QueryOptimizer optimizer) {
        Objects.requireNonNull(pipelineCompiler, "pipelineCompiler must not be null");
        Objects.requireNonNull(optimizer, "optimizer must not be null");
        this.pipelineCompiler = pipelineCompiler;
        this.optimizer = optimizer;
    }

    public DefaultMongoTemplateAggregationExecutor(QueryOptimizer optimizer) {
        this(new MongoPipelineCompiler(), optimizer);
    }

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
    @Override
    public <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
        Pipeline optimized = this.optimizer.optimize(pipeline);
        Aggregation aggregation = this.pipelineCompiler.compile(optimized);
        AggregationResults<R> results = this.mongoTemplate().aggregate(
            aggregation,
            inputType,
            outputType
        );

        return results.getMappedResults();
    }

    /**
     * Execute aggregation with collection name.
     *
     * @param pipeline       the pipeline to execute
     * @param collectionName the collection name
     * @param outputType     the output result type
     * @param <R>            the output type
     * @return the aggregation results
     */
    @Override
    public <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType) {
        Pipeline optimized = this.optimizer.optimize(pipeline);
        Aggregation aggregation = this.pipelineCompiler.compile(optimized);
        AggregationResults<R> results = this.mongoTemplate().aggregate(
            aggregation,
            collectionName,
            outputType
        );

        return results.getMappedResults();
    }

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
    @Override
    public <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
        Aggregation aggregation = this.pipelineCompiler.compile(pipeline);
        AggregationResults<R> results = this.mongoTemplate().aggregate(
            aggregation,
            inputType,
            outputType
        );

        return results.getMappedResults();
    }
}
