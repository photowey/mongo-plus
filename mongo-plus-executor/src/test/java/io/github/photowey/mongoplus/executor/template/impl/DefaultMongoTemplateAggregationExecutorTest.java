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

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.executor.compiler.PipelineCompiler;
import io.github.photowey.mongoplus.optimizer.QueryOptimizer;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * DefaultMongoTemplateAggregationExecutorTest - Unit tests for aggregation executor behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("DefaultMongoTemplateAggregationExecutorTest")
class DefaultMongoTemplateAggregationExecutorTest {

    @Test
    @DisplayName(
        "Given null collaborators, when constructing the executor, then throws a null pointer "
            + "exception"
    )
    @Story("Throw NPE when collaborators null")
    void givenNullCollaborators_whenConstructingTheExecutor_thenThrowsANullPointerException() {
        // Given

        // When
        NullPointerException compilerError = assertThrows(
            NullPointerException.class,
            () -> new DefaultMongoTemplateAggregationExecutor(null, new QueryOptimizer(false))
        );
        NullPointerException optimizerError = assertThrows(
            NullPointerException.class,
            () -> new DefaultMongoTemplateAggregationExecutor(mock(PipelineCompiler.class), null)
        );

        // Then
        assertEquals("pipelineCompiler must not be null", compilerError.getMessage());
        assertEquals("optimizer must not be null", optimizerError.getMessage());
    }

    @Test
    @DisplayName(
        "Given typed aggregation execution, when executing the pipeline, then uses the optimizer "
            + "compiler and mongo template"
    )
    @Story("Typed aggregation uses optimizer and MongoTemplate")
    void givenTypedAggregationExecution_whenExecutingThePipeline_thenUsesTheOptimizerCompilerAndMongoTemplate() {
        // Given
        QueryOptimizer optimizer = mock(QueryOptimizer.class);
        PipelineCompiler compiler = mock(PipelineCompiler.class);
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        Pipeline pipeline = Pipeline.of(new MatchStage(new Document("status", "ACTIVE")));
        Pipeline optimized = Pipeline.of(new MatchStage(new Document("status", "OPTIMIZED")));
        Aggregation aggregation = mock(Aggregation.class);
        AggregationResults<String> results = new AggregationResults<>(List.of("A", "B"), new Document());
        DefaultMongoTemplateAggregationExecutor executor =
            new DefaultMongoTemplateAggregationExecutor(compiler, optimizer);
        executor.setMongoTemplate(mongoTemplate);
        when(optimizer.optimize(pipeline)).thenReturn(optimized);
        when(compiler.compile(optimized)).thenReturn(aggregation);
        when(mongoTemplate.aggregate(aggregation, InputDocument.class, String.class)).thenReturn(results);

        // When
        List<String> actual = executor.execute(pipeline, InputDocument.class, String.class);

        // Then
        assertEquals(List.of("A", "B"), actual);
        verify(optimizer).optimize(pipeline);
        verify(compiler).compile(optimized);
        verify(mongoTemplate).aggregate(aggregation, InputDocument.class, String.class);
    }

    @Test
    @DisplayName(
        "Given raw aggregation execution, when executing the pipeline, then skips optimization "
            + "and uses the original pipeline"
    )
    @Story("Raw aggregation skips optimization and uses original pipeline")
    void givenRawAggregationExecution_whenExecutingThePipeline_thenSkipsOptimizationAndUsesTheOriginalPipeline() {
        // Given
        QueryOptimizer optimizer = mock(QueryOptimizer.class);
        PipelineCompiler compiler = mock(PipelineCompiler.class);
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        Pipeline pipeline = Pipeline.of(new MatchStage(new Document("status", "RAW")));
        Aggregation aggregation = mock(Aggregation.class);
        AggregationResults<String> results = new AggregationResults<>(List.of("RAW"), new Document());
        DefaultMongoTemplateAggregationExecutor executor =
            new DefaultMongoTemplateAggregationExecutor(compiler, optimizer);
        executor.setMongoTemplate(mongoTemplate);
        when(compiler.compile(pipeline)).thenReturn(aggregation);
        when(mongoTemplate.aggregate(aggregation, InputDocument.class, String.class)).thenReturn(results);

        // When
        List<String> actual = executor.executeRaw(pipeline, InputDocument.class, String.class);

        // Then
        assertEquals(List.of("RAW"), actual);
        verify(compiler).compile(pipeline);
        verifyNoInteractions(optimizer);
        verify(mongoTemplate).aggregate(aggregation, InputDocument.class, String.class);
    }

    @Test
    @DisplayName(
        "Given collection aggregation execution, when executing the pipeline, then uses the "
            + "collection overload and default compiler constructor"
    )
    @Story("Collection aggregation uses collection overload")
    void givenCollectionAggregationExecution_whenExecutingThePipeline_thenUsesTheCollectionOverload(
    ) {
        // Given
        QueryOptimizer optimizer = mock(QueryOptimizer.class);
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        Pipeline pipeline = Pipeline.of(new MatchStage(new Document("kind", "COLLECTION")));
        AggregationResults<String> results = new AggregationResults<>(List.of("C"), new Document());
        DefaultMongoTemplateAggregationExecutor executor =
            new DefaultMongoTemplateAggregationExecutor(optimizer);
        executor.setMongoTemplate(mongoTemplate);
        when(optimizer.optimize(pipeline)).thenReturn(pipeline);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("users"), eq(String.class))).thenReturn(results);

        // When
        List<String> actual = executor.execute(pipeline, "users", String.class);

        // Then
        assertSame(results.getMappedResults(), actual);
        verify(optimizer).optimize(pipeline);
        verify(mongoTemplate).aggregate(any(Aggregation.class), eq("users"), eq(String.class));
    }

    private static final class InputDocument {
    }
}
