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

import io.github.photowey.mongoplus.aggregation.stage.LookupStage;
import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.ProjectStage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MatchPushDownRuleTest - Unit tests for adjacent match push-down behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MatchPushDownRuleTest")
class MatchPushDownRuleTest {

    @Test
    @DisplayName(
        "Given project or lookup followed by match, when applying the rule, then swaps the "
            + "adjacent stages"
    )
    @Story("Swap project or lookup followed by match stages")
    void givenProjectOrLookupFollowedByMatch_whenApplyingTheRule_thenSwapsTheAdjacentStages() {
        // Given
        MatchPushDownRule rule = new MatchPushDownRule();
        Pipeline pipeline = Pipeline.builder()
            .add(new ProjectStage("name"))
            .add(new MatchStage(new Document("status", "ACTIVE")))
            .add(new LookupStage("orders", "userId", "ownerId", "orders"))
            .add(new MatchStage(new Document("kind", "VIP")))
            .build();

        // When
        boolean matches = rule.matches(pipeline);
        Pipeline optimized = rule.apply(pipeline);

        // Then
        assertTrue(matches);
        assertEquals(StageType.MATCH, optimized.getStages().get(0).type());
        assertEquals(StageType.PROJECT, optimized.getStages().get(1).type());
        assertEquals(StageType.MATCH, optimized.getStages().get(2).type());
        assertEquals(StageType.LOOKUP, optimized.getStages().get(3).type());
        assertEquals("MatchPushDownRule", rule.getName());
        assertEquals(20, rule.getPriority());
    }

    @Test
    @DisplayName(
        "Given short or already ordered pipelines, when matching, then returns false without "
            + "changing the pipeline"
    )
    @Story("Short or already ordered pipelines return false without change")
    void givenShortOrAlreadyOrderedPipelines_whenMatching_thenReturnsFalseWithoutChangingThePipeline() {
        // Given
        MatchPushDownRule rule = new MatchPushDownRule();
        Pipeline shortPipeline = Pipeline.of(new MatchStage(new Document("status", "ACTIVE")));
        Pipeline orderedPipeline = Pipeline.builder()
            .add(new MatchStage(new Document("status", "ACTIVE")))
            .add(new ProjectStage("name"))
            .build();

        // When
        boolean shortMatch = rule.matches(shortPipeline);
        boolean orderedMatch = rule.matches(orderedPipeline);
        Pipeline optimized = rule.apply(orderedPipeline);

        // Then
        assertFalse(shortMatch);
        assertFalse(orderedMatch);
        assertEquals(2, optimized.getStages().size());
        assertEquals(StageType.MATCH, optimized.getStages().get(0).type());
        assertEquals(StageType.PROJECT, optimized.getStages().get(1).type());
    }
}
