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
package io.github.photowey.mongoplus.core.metadata;

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
 * FieldMetadataTest - Unit tests for field metadata resolution helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("FieldMetadataTest")
class FieldMetadataTest {

    @Test
    @DisplayName(
        "Given field metadata with explicit path and parent, when resolving metadata helpers, "
            + "then returns the configured values"
    )
    @Story("Resolve field metadata with explicit path and parent")
    void givenFieldMetadataWithExplicitPathAndParent_whenResolvingMetadataHelpers_thenReturnsTheConfiguredValues() {
        // Given
        FieldMetadata<Object> parent = FieldMetadata.builder().name("address").column("address").build();
        FieldMetadata<String> field = FieldMetadata.<String>builder()
            .name("city")
            .column("city_name")
            .path("address.city")
            .parent(parent)
            .build();

        // When
        String resolvedPath = field.resolvePath();
        String resolvedColumn = field.resolveColumn();
        boolean nested = field.isNested();

        // Then
        assertEquals("address.city", resolvedPath);
        assertEquals("city_name", resolvedColumn);
        assertTrue(nested);
    }

    @Test
    @DisplayName(
        "Given field metadata without explicit path or column, when resolving metadata helpers, "
            + "then falls back to the field name"
    )
    @Story("Fall back to field name when path or column not set")
    void givenFieldMetadataWithoutExplicitPathOrColumn_whenResolvingMetadataHelpers_thenFallsBackToTheFieldName() {
        // Given
        FieldMetadata<String> field = FieldMetadata.<String>builder()
            .name("status")
            .column("")
            .path("")
            .build();

        // When
        String resolvedPath = field.resolvePath();
        String resolvedColumn = field.resolveColumn();
        boolean nested = field.isNested();

        // Then
        assertEquals("status", resolvedPath);
        assertEquals("status", resolvedColumn);
        assertFalse(nested);
    }
}
