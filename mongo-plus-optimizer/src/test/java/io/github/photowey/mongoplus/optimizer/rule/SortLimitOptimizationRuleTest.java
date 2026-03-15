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
package io.github.photowey.mongoplus.optimizer.rule;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.LimitStage;
import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.SortStage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SortLimitOptimizationRuleTest - Unit tests for sort and limit optimization behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("SortLimitOptimizationRuleTest")
class SortLimitOptimizationRuleTest {

    @Test
    @DisplayName(
        "Given sort limit and redundant sorts, when applying the rule, then keeps only the last "
            + "sort stage"
    )
    @Story("Keep only last sort stage when redundant sorts present")
    void givenSortLimitAndRedundantSorts_whenApplyingTheRule_thenKeepsOnlyTheLastSortStage() {
        // Given
        SortLimitOptimizationRule rule = new SortLimitOptimizationRule();
        Pipeline pipeline = Pipeline.builder()
            .add(new SortStage("age", 1))
            .add(new LimitStage(5))
            .add(new SortStage("score", -1))
            .build();

        // When
        boolean matches = rule.matches(pipeline);
        Pipeline optimized = rule.apply(pipeline);

        // Then
        assertTrue(matches);
        assertEquals(2, optimized.getStages().size());
        assertEquals(StageType.LIMIT, optimized.getStages().get(0).type());
        assertEquals(StageType.SORT, optimized.getStages().get(1).type());
        assertEquals("SortLimitOptimizationRule", rule.getName());
        assertEquals(40, rule.getPriority());
    }

    @Test
    @DisplayName(
        "Given a pipeline without sort limit adjacency or duplicate sorts, when matching, then "
            + "returns false"
    )
    @Story("Pipeline without sort-limit adjacency returns false")
    void givenAPipelineWithoutSortLimitAdjacencyOrDuplicateSorts_whenMatching_thenReturnsFalse() {
        // Given
        SortLimitOptimizationRule rule = new SortLimitOptimizationRule();
        Pipeline pipeline = Pipeline.builder()
            .add(new MatchStage(new Document("status", "ACTIVE")))
            .add(new SortStage("score", -1))
            .build();

        // When
        boolean matches = rule.matches(pipeline);

        // Then
        assertFalse(matches);
    }
}
