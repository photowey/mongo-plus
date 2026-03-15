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
package io.github.photowey.mongoplus.core.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * MongoPlusExceptionTest - Unit tests for MongoPlus exception constructors and fluent mutation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MongoPlusExceptionTest")
class MongoPlusExceptionTest {

    @Test
    @DisplayName(
        "Given message based constructors, when creating the exception, then stores the expected "
            + "error code"
    )
    @Story("Store error code via message constructors")
    void givenMessageBasedConstructors_whenCreatingTheException_thenStoresTheExpectedErrorCode() {
        // Given

        // When
        MongoPlusException defaultError = new MongoPlusException("boom");
        MongoPlusException codedError = new MongoPlusException("boom", "mongo.error");
        MongoPlusException causeError = new MongoPlusException("boom", new IllegalArgumentException("bad"));

        // Then
        assertEquals("UNKNOWN_ERROR", defaultError.getErrorCode());
        assertEquals("mongo.error", codedError.getErrorCode());
        assertEquals("UNKNOWN_ERROR", causeError.getErrorCode());
        assertEquals("bad", causeError.getCause().getMessage());
    }

    @Test
    @DisplayName(
        "Given an entity class, when setting it on the exception, then returns the same "
            + "exception instance"
    )
    @Story("Set and return entity class on exception")
    void givenAnEntityClass_whenSettingItOnTheException_thenReturnsTheSameExceptionInstance() {
        // Given
        MongoPlusException error = new MongoPlusException("boom");

        // When
        MongoPlusException actual = error.setEntityClass(MongoPlusExceptionTest.class);

        // Then
        assertSame(error, actual);
        assertSame(MongoPlusExceptionTest.class, actual.getEntityClass());
    }
}
