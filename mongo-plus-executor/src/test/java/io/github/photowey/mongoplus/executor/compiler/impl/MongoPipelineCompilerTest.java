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
package io.github.photowey.mongoplus.executor.compiler.impl;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import io.github.photowey.mongoplus.aggregation.stage.GroupStage;
import io.github.photowey.mongoplus.aggregation.stage.LimitStage;
import io.github.photowey.mongoplus.aggregation.stage.LookupStage;
import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.ProjectStage;
import io.github.photowey.mongoplus.aggregation.stage.SkipStage;
import io.github.photowey.mongoplus.aggregation.stage.SortStage;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MongoPipelineCompilerTest - Unit tests for MongoPipelineCompiler aggregation conversion.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MongoPipelineCompilerTest")
class MongoPipelineCompilerTest {

    @Test
    @DisplayName("Given null or empty pipeline, when compiling, then returns an empty aggregation pipeline")
    @Story("Return empty pipeline for null or empty input")
    void givenNullOrEmptyPipeline_whenCompiling_thenReturnsAnEmptyAggregationPipeline() {
        // Given
        MongoPipelineCompiler compiler = new MongoPipelineCompiler();
        Pipeline emptyPipeline = Pipeline.builder().build();

        // When
        Aggregation nullAggregation = compiler.compile(null);
        Aggregation emptyAggregation = compiler.compile(emptyPipeline);

        // Then
        assertTrue(nullAggregation.toPipeline(Aggregation.DEFAULT_CONTEXT).isEmpty());
        assertTrue(emptyAggregation.toPipeline(Aggregation.DEFAULT_CONTEXT).isEmpty());
    }

    @Test
    @DisplayName("Given all supported stages, when compiling, then returns matching MongoDB stage documents")
    @Story("Compile supported stages to Mongo stage documents")
    void givenAllSupportedStages_whenCompiling_thenReturnsMatchingMongoDbStageDocuments() {
        // Given
        MongoPipelineCompiler compiler = new MongoPipelineCompiler();
        Pipeline pipeline = Pipeline.of(
            new MatchStage(new Document("status", "ACTIVE")),
            new GroupStage("status").count("total"),
            new ProjectStage("status").computed("alias", "$status"),
            new SortStage("total", -1),
            new LimitStage(5),
            new SkipStage(2),
            new LookupStage("orders", "userId", "ownerId", "orders")
        );

        // When
        Aggregation aggregation = compiler.compile(pipeline);
        List<Document> documents = aggregation.toPipeline(Aggregation.DEFAULT_CONTEXT);

        // Then
        assertEquals(7, documents.size());
        assertEquals("ACTIVE", documents.get(0).get(MongoPlusConstants.MATCH, Document.class).getString("status"));
        assertEquals("$status", documents.get(1).get(MongoPlusConstants.GROUP, Document.class).get("_id"));
        assertEquals("$status", documents.get(2).get(MongoPlusConstants.PROJECT, Document.class).getString("alias"));
        assertEquals(-1, documents.get(3).get(MongoPlusConstants.SORT, Document.class).getInteger("total"));
        assertEquals(5L, documents.get(4).get(MongoPlusConstants.LIMIT));
        assertEquals(2L, documents.get(5).get(MongoPlusConstants.SKIP));
        assertEquals(
            "orders",
            documents.get(6).get(MongoPlusConstants.LOOKUP, Document.class).getString("from")
        );
    }

    @Test
    @DisplayName("Given malformed stage payloads, when compiling, then skips stages without usable aggregation data")
    @Story("Skip malformed stages without usable data")
    void givenMalformedStagePayloads_whenCompiling_thenSkipsStagesWithoutUsableAggregationData() {
        // Given
        MongoPipelineCompiler compiler = new MongoPipelineCompiler();
        Pipeline pipeline = Pipeline.of(
            this.stage(StageType.MATCH, new Document()),
            this.stage(StageType.GROUP, new Document()),
            this.stage(StageType.PROJECT, new Document()),
            this.stage(StageType.SORT, new Document()),
            this.stage(StageType.LIMIT, new Document()),
            this.stage(StageType.SKIP, new Document()),
            this.stage(StageType.LOOKUP, new Document())
        );

        // When
        Aggregation aggregation = compiler.compile(pipeline);
        List<Document> documents = aggregation.toPipeline(Aggregation.DEFAULT_CONTEXT);

        // Then
        assertTrue(documents.isEmpty());
    }

    private Stage stage(StageType type, Document bson) {
        return new Stage() {
            @Override
            public StageType getType() {
                return type;
            }

            @Override
            public Document toBson() {
                return bson;
            }
        };
    }
}
