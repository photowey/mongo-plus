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
package io.github.photowey.mongoplus.wrapper.core.segment;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QuerySegmentTest - Unit tests for query segment tree construction and traversal.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QuerySegmentTest")
class QuerySegmentTest {

    @Test
    @DisplayName(
        "Given nested query segments, when traversing the tree, then parent links and collected "
            + "conditions are preserved"
    )
    @Story("Traverse nested query segment tree preserves parent links")
    void givenNestedQuerySegments_whenTraversingTheTree_thenParentLinksAndCollectedConditionsArePreserved() {
        // Given
        Condition active = Condition.builder().field("status").operator(Operator.EQ).value("ACTIVE").build();
        Condition age = Condition.builder().field("age").operator(Operator.GT).value(18).build();
        Condition city = Condition.builder().field("city").operator(Operator.EQ).value("Shanghai").build();
        QuerySegment root = QuerySegment.root();
        QuerySegment condition = QuerySegment.condition(active);
        List<Segment> visited = new ArrayList<>();

        // When
        root.addChild(condition)
            .addCondition(age)
            .and(nested -> nested.addCondition(city))
            .or(
                nested -> nested.addCondition(
                    Condition.builder().field("vip").operator(Operator.EQ).value(true).build()
                )
            );
        root.traverse(segment -> visited.add(segment.getType()));
        List<Condition> conditions = root.getAllConditions();

        // Then
        assertTrue(root.isRoot());
        assertTrue(condition.isCondition());
        assertSame(root, condition.getParent());
        assertEquals(4, conditions.size());
        assertTrue(visited.contains(Segment.ROOT));
        assertTrue(visited.contains(Segment.CONDITION));
        assertTrue(visited.contains(Segment.AND));
        assertTrue(visited.contains(Segment.OR));
    }

    @Test
    @DisplayName(
        "Given logical and condition segments, when checking flags, then reports the correct "
            + "segment role"
    )
    @Story("Logical AND condition segments report correct role")
    void givenLogicalAndConditionSegments_whenCheckingFlags_thenReportsTheCorrectSegmentRole() {
        // Given
        QuerySegment andSegment = QuerySegment.and();
        QuerySegment orSegment = QuerySegment.or();
        QuerySegment conditionSegment = QuerySegment.condition(
            Condition.builder().field("name").operator(Operator.EQ).value("photowey").build()
        );

        // When
        boolean andLogical = andSegment.isLogical();
        boolean orLogical = orSegment.isLogical();
        boolean conditionLeaf = conditionSegment.isCondition();

        // Then
        assertTrue(andLogical);
        assertTrue(orLogical);
        assertTrue(conditionLeaf);
    }
}
