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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * EntityResolverTest - Tests for metadata resolution SPI and cache facade.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("EntityResolverTest")
class EntityResolverTest {

    @AfterEach
    void clearResolver() {
        EntityResolver.resetEntityMetadataResolver();
        EntityResolver.clearCache();
    }

    @Test
    @DisplayName("Given plain entity When resolving with default resolver Then uses framework neutral conventions")
    @Story("Resolve plain entity with default resolver conventions")
    void givenPlainEntity_whenResolvingWithDefaultResolver_thenUsesFrameworkNeutralConventions() {
        EntityMetadata metadata = EntityResolver.resolve(UserAccount.class);

        assertEquals(UserAccount.class, metadata.getEntityClass());
        assertEquals("userAccount", metadata.getCollectionName());
        assertEquals("id", metadata.getIdField());
        assertEquals(ID, metadata.getIdColumn());
        assertEquals("emailAddress", metadata.getColumnName("emailAddress"));
        assertTrue(EntityResolver.isCached(UserAccount.class));
    }

    @Test
    @DisplayName("Given custom resolver When installed Then cache facade delegates to it")
    @Story("Custom resolver delegates to cache facade")
    void givenCustomResolver_whenInstalled_thenCacheFacadeDelegatesToIt() {
        EntityMetadata expected = EntityMetadata.builder()
            .entityClass(UserAccount.class)
            .collectionName("custom_users")
            .idField("identifier")
            .idColumn("identifier")
            .build();
        expected.addFieldMapping("identifier", "identifier");

        EntityMetadataResolver resolver = entityClass -> expected;
        EntityResolver.setEntityMetadataResolver(resolver);

        EntityMetadata metadata = EntityResolver.resolve(UserAccount.class);

        assertSame(resolver, EntityResolver.getEntityMetadataResolver());
        assertSame(expected, metadata);
        assertSame(expected, EntityResolver.getCached(UserAccount.class));
    }

    private static final class UserAccount {

        private Long id;
        private String emailAddress;
    }
}
