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
package io.github.photowey.mongoplus.aggregation;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.GroupStage;
import io.github.photowey.mongoplus.aggregation.stage.LookupStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.ProjectStage;
import io.github.photowey.mongoplus.aggregation.stage.SortStage;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StageValueObjectTest - Unit tests for aggregation stage value objects and pipeline helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("StageValueObjectTest")
class StageValueObjectTest {

    @Test
    @DisplayName(
        "Given group stage accumulators, when serializing to bson, then includes all accumulator "
            + "definitions"
    )
    @Story("Group stage accumulators serialize to BSON with all definitions")
    void givenGroupStageAccumulators_whenSerializingToBson_thenIncludesAllAccumulatorDefinitions() {
        // Given
        GroupStage stage = new GroupStage("dept")
            .sum("salary", "totalSalary")
            .avg("age", "avgAge")
            .count("employeeCount")
            .max("salary", "maxSalary")
            .min("salary", "minSalary");
        stage.addSum("bonus", "bonusTotal");
        stage.addAvg("level", "avgLevel");
        stage.addCount("countAgain");
        stage.addMax("score", "bestScore");
        stage.addMin("score", "lowestScore");

        // When
        Document group = stage.toBson().get(MongoPlusConstants.GROUP, Document.class);

        // Then
        assertEquals("$dept", group.get(MongoPlusConstants.ID));
        assertTrue(group.containsKey("totalSalary"));
        assertTrue(group.containsKey("avgAge"));
        assertTrue(group.containsKey("employeeCount"));
        assertTrue(group.containsKey("bestScore"));
        assertEquals(StageType.GROUP, stage.type());
    }

    @Test
    @DisplayName(
        "Given project sort and lookup stages, when serializing to bson, then preserves stage "
            + "specific fields"
    )
    @Story("Project sort and lookup stages preserve stage-specific fields in BSON")
    void givenProjectSortAndLookupStages_whenSerializingToBson_thenPreservesStageSpecificFields() {
        // Given
        ProjectStage projectStage = new ProjectStage("name").exclude("password").computed("alias", "$name");
        SortStage sortStage = new SortStage("age", 1).desc("createdAt");
        LookupStage lookupStage = new LookupStage("orders", "userId", "ownerId", "orders");

        // When
        Document project = projectStage.toBson().get(MongoPlusConstants.PROJECT, Document.class);
        Document sort = sortStage.toBson().get(MongoPlusConstants.SORT, Document.class);
        Document lookup = lookupStage.toBson().get(MongoPlusConstants.LOOKUP, Document.class);

        // Then
        assertEquals(1, project.get("name"));
        assertEquals(0, project.get("password"));
        assertEquals("$name", project.get("alias"));
        assertEquals(1, sort.get("age"));
        assertEquals(-1, sort.get("createdAt"));
        assertEquals("orders", lookup.get("from"));
        assertEquals(StageType.PROJECT, projectStage.type());
        assertEquals(StageType.SORT, sortStage.type());
        assertEquals(StageType.LOOKUP, lookupStage.type());
    }

    @Test
    @DisplayName(
        "Given pipeline factory methods and builder helpers, when constructing the pipeline, then "
            + "keeps stage order and immutable accessors"
    )
    @Story("Pipeline factory methods keep stage order and accessors")
    void givenPipelineFactoryMethodsAndBuilderHelpers_whenConstructingThePipeline_thenKeepsStageOrderAndAccessors(
    ) {
        // Given
        Stage project = new ProjectStage("name");
        Stage sort = new SortStage().asc("age");
        Pipeline arrayPipeline = Pipeline.of(project, sort);
        Pipeline collectionPipeline = Pipeline.of(List.of(project, sort));
        Pipeline singlePipeline = Pipeline.of(project);
        Pipeline builtPipeline = Pipeline.builder().add(project).add(sort).build();
        Pipeline emptyPipeline = Pipeline.builder().build();

        // When
        List<Document> bsonArray = builtPipeline.toBsonArray();

        // Then
        assertEquals(2, arrayPipeline.getStages().size());
        assertEquals(2, collectionPipeline.stages().size());
        assertEquals(1, singlePipeline.getStages().size());
        assertEquals(2, bsonArray.size());
        assertSame(project, arrayPipeline.getStages().get(0));
        assertSame(sort, arrayPipeline.stages().get(1));
        assertFalse(arrayPipeline.isEmpty());
        assertTrue(emptyPipeline.isEmpty());
    }
}
