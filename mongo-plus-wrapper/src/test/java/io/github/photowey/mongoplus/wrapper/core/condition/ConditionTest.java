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
package io.github.photowey.mongoplus.wrapper.core.condition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * ConditionTest - Unit tests for condition fluent accessors.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("ConditionTest")
class ConditionTest {

    @Test
    @DisplayName(
        "Given a built condition, when accessing fluent aliases, then returns the stored "
            + "condition state"
    )
    @Story("Access fluent condition aliases returns stored state")
    void givenABuiltCondition_whenAccessingFluentAliases_thenReturnsTheStoredConditionState() {
        // Given
        QueryWrapper<Object> nestedWrapper = new QueryWrapper<>();
        Condition condition = Condition.builder()
            .field("status")
            .operator(Operator.BETWEEN)
            .value(1)
            .secondValue(10)
            .nestedWrapper(nestedWrapper)
            .segment(Segment.OR)
            .build();

        // When
        String field = condition.field();
        Operator operator = condition.operator();
        Object value = condition.value();
        Object secondValue = condition.secondValue();
        Object actualNestedWrapper = condition.nestedWrapper();
        Segment segment = condition.segment();

        // Then
        assertEquals("status", field);
        assertEquals(Operator.BETWEEN, operator);
        assertEquals(1, value);
        assertEquals(10, secondValue);
        assertSame(nestedWrapper, actualNestedWrapper);
        assertEquals(Segment.OR, segment);
    }
}
