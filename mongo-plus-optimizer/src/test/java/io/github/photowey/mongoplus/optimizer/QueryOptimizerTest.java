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
package io.github.photowey.mongoplus.optimizer;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.LimitStage;
import io.github.photowey.mongoplus.aggregation.stage.MatchStage;
import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.optimizer.rule.MatchMergeRule;
import io.github.photowey.mongoplus.optimizer.rule.MatchPushDownRule;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.GT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QueryOptimizerTest - Tests for QueryOptimizer.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QueryOptimizerTest")
class QueryOptimizerTest {

    @Test
    @DisplayName("Given default rules When new QueryOptimizer true Then rules not empty")
    @Story("Default rules when optimizer created with true")
    void givenDefaultRules_whenNewQueryOptimizerTrue_thenRulesNotEmpty() {
        QueryOptimizer optimizer = new QueryOptimizer(true);

        assertNotNull(optimizer);
        assertFalse(optimizer.getRules().isEmpty());
    }

    @Test
    @DisplayName("Given no default rules When new QueryOptimizer false Then rules empty")
    @Story("No default rules when optimizer created with false")
    void givenNoDefaultRules_whenNewQueryOptimizerFalse_thenRulesEmpty() {
        QueryOptimizer optimizer = new QueryOptimizer(false);

        assertNotNull(optimizer);
        assertTrue(optimizer.getRules().isEmpty());
    }

    @Test
    @DisplayName("Given empty optimizer When addRule Then rules size one")
    @Story("Add rule to empty optimizer")
    void givenEmptyOptimizer_whenAddRule_thenRulesSizeOne() {
        QueryOptimizer optimizer = new QueryOptimizer(false);
        optimizer.addRule(new MatchMergeRule());

        assertEquals(1, optimizer.getRules().size());
    }

    @Test
    @DisplayName("Given default optimizer When removeRule Then size decrements")
    @Story("Remove rule decrements size")
    void givenDefaultOptimizer_whenRemoveRule_thenSizeDecrements() {
        QueryOptimizer optimizer = new QueryOptimizer(true);
        int initialSize = optimizer.getRules().size();

        optimizer.removeRule(MatchMergeRule.class);

        assertEquals(initialSize - 1, optimizer.getRules().size());
    }

    @Test
    @DisplayName("Given optimizer with rules When clearRules Then rules empty")
    @Story("Clear rules empties optimizer")
    void givenOptimizerWithRules_whenClearRules_thenRulesEmpty() {
        QueryOptimizer optimizer = new QueryOptimizer(true);
        optimizer.clearRules();

        assertTrue(optimizer.getRules().isEmpty());
    }

    @Test
    @DisplayName("Given empty pipeline When optimize Then result empty")
    @Story("Empty pipeline optimizes to empty result")
    void givenEmptyPipeline_whenOptimize_thenResultEmpty() {
        QueryOptimizer optimizer = new QueryOptimizer(true);
        Pipeline pipeline = Pipeline.of();

        Pipeline result = optimizer.optimize(pipeline);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given two match stages When optimize with MatchMergeRule Then result not null")
    @Story("Merge two match stages with match merge rule")
    void givenTwoMatchStages_whenOptimizeWithMatchMergeRule_thenResultNotNull() {
        QueryOptimizer optimizer = new QueryOptimizer(false);
        optimizer.addRule(new MatchMergeRule());

        Pipeline pipeline = Pipeline.builder()
            .add(new MatchStage(new Document("status", "active")))
            .add(new MatchStage(new Document("age", new Document(GT, 18))))
            .build();

        Pipeline result = optimizer.optimize(pipeline);

        assertNotNull(result);
        // After optimization, two match stages should be merged
    }

    @Test
    @DisplayName("Given match and limit When optimize with MatchPushDown Then two stages")
    @Story("Match push down produces two stages")
    void givenMatchAndLimit_whenOptimizeWithMatchPushDown_thenTwoStages() {
        QueryOptimizer optimizer = new QueryOptimizer(false);
        optimizer.addRule(new MatchPushDownRule());

        // Pipeline without match after project won't match
        Pipeline pipeline = Pipeline.builder()
            .add(new MatchStage(new Document("status", "active")))
            .add(new LimitStage(10))
            .build();

        Pipeline result = optimizer.optimize(pipeline);

        assertNotNull(result);
        assertEquals(2, result.getStages().size());
    }

    @Test
    @DisplayName("Given two rules When addRule Then priorities match expected")
    @Story("Add rule priorities match expected")
    void givenTwoRules_whenAddRule_thenPrioritiesMatchExpected() {
        QueryOptimizer optimizer = new QueryOptimizer(false);

        MatchMergeRule mergeRule = new MatchMergeRule();
        MatchPushDownRule pushDownRule = new MatchPushDownRule();

        optimizer.addRule(pushDownRule);
        optimizer.addRule(mergeRule);

        // Rules should be sorted by priority
        assertEquals(mergeRule.getPriority(), 10);
        assertEquals(pushDownRule.getPriority(), 20);
    }
}

