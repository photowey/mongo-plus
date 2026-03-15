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

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.ProjectStage;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ProjectSimplifyRuleTest - Unit tests for redundant project elimination behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ProjectSimplifyRuleTest")
class ProjectSimplifyRuleTest {

    @Test
    @DisplayName(
        "Given redundant and non-redundant project stages, when matching and applying the rule, "
            + "then removes only the redundant project"
    )
    @Story("Non-redundant project stages keep pipeline unchanged")
    void givenRedundantAndNonRedundantProjectStages_whenMatchingAndApplyingTheRule_thenRemovesOnlyTheRedundantProject(
    ) {
        // Given
        ProjectSimplifyRule rule = new ProjectSimplifyRule();
        Pipeline pipeline = Pipeline.builder()
            .add(this.projectStage(new Document(MongoPlusConstants.ID, 0)))
            .add(new ProjectStage("name"))
            .build();

        // When
        boolean matches = rule.matches(pipeline);
        Pipeline optimized = rule.apply(pipeline);

        // Then
        assertTrue(matches);
        assertEquals(1, optimized.getStages().size());
        assertEquals(StageType.PROJECT, optimized.getStages().get(0).type());
        assertEquals("ProjectSimplifyRule", rule.getName());
        assertEquals(30, rule.getPriority());
    }

    @Test
    @DisplayName(
        "Given non-redundant project stages, when evaluating the rule, then reports no match and "
            + "keeps the pipeline unchanged"
    )
    @Story("Non-redundant project stages keep pipeline unchanged")
    void givenNonRedundantProjectStages_whenEvaluatingTheRule_thenReportsNoMatchAndKeepsThePipelineUnchanged() {
        // Given
        ProjectSimplifyRule rule = new ProjectSimplifyRule();
        Pipeline pipeline = Pipeline.builder()
            .add(new ProjectStage("name", "age"))
            .add(new ProjectStage().computed("score", "$score"))
            .build();

        // When
        boolean matches = rule.matches(pipeline);
        Pipeline optimized = rule.apply(pipeline);

        // Then
        assertFalse(matches);
        assertEquals(2, optimized.getStages().size());
    }

    private Stage projectStage(Document projectDoc) {
        return new Stage() {
            @Override
            public StageType getType() {
                return StageType.PROJECT;
            }

            @Override
            public Document toBson() {
                return new Document(MongoPlusConstants.PROJECT, projectDoc);
            }
        };
    }
}
