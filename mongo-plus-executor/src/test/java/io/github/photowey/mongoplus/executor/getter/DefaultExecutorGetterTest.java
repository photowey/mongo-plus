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
package io.github.photowey.mongoplus.executor.getter;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * DefaultExecutorGetterTest - Unit tests for DefaultExecutorGetter accessor behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("DefaultExecutorGetterTest")
class DefaultExecutorGetterTest {

    @Test
    @DisplayName(
        "Given query and aggregation executors, when accessing getter methods, then returns "
            + "the same instances"
    )
    @Story("Getter returns same query and aggregation executor instances")
    void givenQueryAndAggregationExecutors_whenAccessingGetterMethods_thenReturnsTheSameInstances() {
        // Given
        QueryExecutor queryExecutor = this.proxy(QueryExecutor.class);
        AggregationExecutor aggregationExecutor = this.proxy(AggregationExecutor.class);
        DefaultExecutorGetter getter = new DefaultExecutorGetter(queryExecutor, aggregationExecutor);

        // When
        QueryExecutor actualQueryExecutor = getter.queryExecutor();
        AggregationExecutor actualAggregationExecutor = getter.aggregationExecutor();

        // Then
        assertSame(queryExecutor, actualQueryExecutor);
        assertSame(aggregationExecutor, actualAggregationExecutor);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type) {
        return (T) Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] {type},
            (proxy, method, args) -> null
        );
    }
}
