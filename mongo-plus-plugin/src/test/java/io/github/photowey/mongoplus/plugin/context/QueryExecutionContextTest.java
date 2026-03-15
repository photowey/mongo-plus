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
package io.github.photowey.mongoplus.plugin.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.plugin.enums.InvocationType;
import io.github.photowey.mongoplus.plugin.enums.QueryExecutionOperation;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * QueryExecutionContextTest - Unit tests for wrapper and page lookup helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QueryExecutionContextTest")
class QueryExecutionContextTest {

    @Test
    @DisplayName("Given wrapper and page arguments, when resolving helper accessors, then returns the matching values")
    @Story("Resolve wrapper and page from helper accessors")
    void givenWrapperAndPageArguments_whenResolvingHelperAccessors_thenReturnsTheMatchingValues() {
        // Given
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        Page<String> page = new Page<>(2, 10);
        QueryExecutionContext context = QueryExecutionContext.builder()
            .operation(QueryExecutionOperation.SELECT_PAGE)
            .arguments(new Object[] {"ignored", page, wrapper})
            .build();

        // When
        InvocationType invocationType = context.getInvocationType();
        Object actualWrapper = context.getWrapper();
        Object actualPage = context.getPage();

        // Then
        assertEquals(InvocationType.QUERY, invocationType);
        assertSame(wrapper, actualWrapper);
        assertSame(page, actualPage);
    }

    @Test
    @DisplayName("Given empty arguments, when resolving helper accessors, then returns null for wrapper and page")
    @Story("Empty arguments return null for wrapper and page")
    void givenEmptyArguments_whenResolvingHelperAccessors_thenReturnsNullForWrapperAndPage() {
        // Given
        QueryExecutionContext context = QueryExecutionContext.builder()
            .operation(QueryExecutionOperation.SELECT_LIST)
            .build();

        // When
        Object actualWrapper = context.getWrapper();
        Object actualPage = context.getPage();

        // Then
        assertNull(actualWrapper);
        assertNull(actualPage);
    }
}
