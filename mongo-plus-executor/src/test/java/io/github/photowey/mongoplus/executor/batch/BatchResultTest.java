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
package io.github.photowey.mongoplus.executor.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BatchResultTest - Unit tests for BatchResult aggregation and error helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/13
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("BatchResultTest")
class BatchResultTest {

    @Test
    @DisplayName(
        "Given counts without errors, when querying result state, then reports success "
            + "and total affected count"
    )
    @Story("Report success and total affected count when no errors")
    void givenCountsWithoutErrors_whenQueryingResultState_thenReportsSuccessAndTotalAffectedCount() {
        // Given
        BatchResult result = BatchResult.builder()
            .insertedCount(1)
            .modifiedCount(2)
            .deletedCount(3)
            .build();

        // When
        boolean success = result.isSuccess();
        int totalAffected = result.getTotalAffected();

        // Then
        assertTrue(success);
        assertEquals(6, totalAffected);
    }

    @Test
    @DisplayName(
        "Given added or factory-created errors, when querying result state, then reports "
            + "failure and stored errors"
    )
    @Story("Report failure and stored errors when errors present")
    void givenAddedOrFactoryCreatedErrors_whenQueryingResultState_thenReportsFailureAndStoredErrors() {
        // Given
        BatchResult result = new BatchResult();

        // When
        result.addError(2, "duplicate key");
        BatchResult errorResult = BatchResult.withError(5, "write concern failed");

        // Then
        assertFalse(result.isSuccess());
        assertEquals(1, result.getErrors().size());
        assertEquals(2, result.getErrors().get(0).getIndex());
        assertEquals("duplicate key", result.getErrors().get(0).getMessage());
        assertFalse(errorResult.isSuccess());
        assertEquals(5, errorResult.getErrors().get(0).getIndex());
        assertEquals("write concern failed", errorResult.getErrors().get(0).getMessage());
    }
}
