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
 * EntityMetadataTest - Unit tests for entity metadata field mapping helpers.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("EntityMetadataTest")
class EntityMetadataTest {

    @Test
    @DisplayName(
        "Given entity metadata mappings, when resolving column and property names, then returns "
            + "the configured mapping"
    )
    @Story("Resolve column and property names from metadata")
    void givenEntityMetadataMappings_whenResolvingColumnAndPropertyNames_thenReturnsTheConfiguredMapping() {
        // Given
        EntityMetadata metadata = EntityMetadata.builder().collectionName("users").build();
        metadata.addFieldMapping("userName", "user_name");

        // When
        String columnName = metadata.getColumnName("userName");
        String propertyName = metadata.getPropertyName("user_name");
        boolean hasFieldMapping = metadata.hasFieldMapping("userName");

        // Then
        assertEquals("user_name", columnName);
        assertEquals("userName", propertyName);
        assertTrue(hasFieldMapping);
    }

    @Test
    @DisplayName(
        "Given missing entity metadata mappings, when resolving column and property names, then "
            + "falls back to the original value"
    )
    @Story("Fall back to original value when metadata missing")
    void givenMissingEntityMetadataMappings_whenResolvingColumnAndPropertyNames_thenFallsBackToTheOriginalValue() {
        // Given
        EntityMetadata metadata = EntityMetadata.builder().collectionName("users").build();

        // When
        String columnName = metadata.getColumnName("status");
        String propertyName = metadata.getPropertyName("status");
        boolean hasFieldMapping = metadata.hasFieldMapping("status");

        // Then
        assertEquals("status", columnName);
        assertEquals("status", propertyName);
        assertFalse(hasFieldMapping);
    }
}
