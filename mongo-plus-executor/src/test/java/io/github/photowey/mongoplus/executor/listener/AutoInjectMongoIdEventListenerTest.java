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
package io.github.photowey.mongoplus.executor.listener;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import io.github.photowey.mongoplus.annotation.BusinessId;
import io.github.photowey.mongoplus.annotation.MongoId;
import io.github.photowey.mongoplus.core.id.SnowflakeIdGenerator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AutoInjectMongoIdEventListenerTest - Tests for MongoIdEventListener.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AutoInjectMongoIdEventListenerTest")
class AutoInjectMongoIdEventListenerTest {

    private final AutoInjectMongoIdEventListener listener = new AutoInjectMongoIdEventListener(
        new SnowflakeIdGenerator()
    );

    @Test
    @DisplayName("Given entity with MongoId ASSIGN_ID When onBeforeConvert Then id assigned")
    @Story("Assign ID for MongoId ASSIGN_ID entity")
    void givenEntityWithMongoIdAssignId_whenOnBeforeConvert_thenIdAssigned() {
        // Test @MongoId with ASSIGN_ID type
        EntityWithMongoId entity = new EntityWithMongoId();
        entity.setName("test");

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertNotNull(entity.getId());
        assertTrue(entity.getId() > 0);
    }

    @Test
    @DisplayName("Given entity with MongoId ASSIGN_UUID When onBeforeConvert Then id 32 chars")
    @Story("Assign 32-char UUID for MongoId ASSIGN_UUID entity")
    void givenEntityWithMongoIdAssignUuid_whenOnBeforeConvert_thenId32Chars() {
        // Test @MongoId with ASSIGN_UUID type
        EntityWithUuid entity = new EntityWithUuid();
        entity.setName("test");

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertNotNull(entity.getId());

        // UUID without dashes
        assertEquals(32, entity.getId().length());
    }

    @Test
    @DisplayName("Given entity with BusinessId When onBeforeConvert Then _id set from business id")
    @Story("Set _id from BusinessId field")
    void givenEntityWithBusinessId_whenOnBeforeConvert_thenIdSetFromBusinessId() {
        // Test @BusinessId - copy business ID to _id
        EntityWithBusinessId entity = new EntityWithBusinessId();
        entity.setOrderNo("ORDER_20240307001");
        entity.setAmount(100.0);

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertNotNull(entity.getId());
        assertEquals("ORDER_20240307001", entity.getId());
    }

    @Test
    @DisplayName("Given entity with id already set When onBeforeConvert Then id not overridden")
    @Story("Do not override pre-set ID")
    void givenEntityWithIdAlreadySet_whenOnBeforeConvert_thenIdNotOverridden() {
        // Test when ID is already set - should not override
        EntityWithMongoId entity = new EntityWithMongoId();
        entity.setId(999L);
        entity.setName("test");

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertEquals(999L, entity.getId());
    }

    @Test
    @DisplayName("Given entity with multiple BusinessId When onBeforeConvert Then throws")
    @Story("Throw when multiple BusinessId present")
    void givenEntityWithMultipleBusinessId_whenOnBeforeConvert_thenThrows() {
        // Test multiple @BusinessId annotations - should throw exception
        assertThrows(IllegalStateException.class, () -> {
            EntityWithMultipleBusinessId entity =
                new EntityWithMultipleBusinessId();
            entity.setOrderNo("ORDER_001");
            entity.setTradeNo("TRADE_001");

            BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
                entity,
                "test_collection"
            );
            listener.onBeforeConvert(event);
        });
    }

    @Test
    @DisplayName("Given BusinessId null When onBeforeConvert Then id not set")
    @Story("Skip ID when BusinessId is null")
    void givenBusinessIdNull_whenOnBeforeConvert_thenIdNotSet() {
        // Test @BusinessId when business ID is null - should not set ID
        EntityWithBusinessId entity = new EntityWithBusinessId();
        entity.setOrderNo(null);
        entity.setAmount(100.0);

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Given BusinessId empty When onBeforeConvert Then id not set")
    @Story("Skip ID when BusinessId is empty")
    void givenBusinessIdEmpty_whenOnBeforeConvert_thenIdNotSet() {
        // Test @BusinessId when business ID is empty - should not set ID
        EntityWithBusinessId entity = new EntityWithBusinessId();
        entity.setOrderNo("");
        entity.setAmount(100.0);

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Given child entity with inherited MongoId When onBeforeConvert Then id assigned")
    @Story("Assign ID for child entity with inherited MongoId")
    void givenChildEntityWithInheritedMongoId_whenOnBeforeConvert_thenIdAssigned() {
        // Test ID generation with inheritance
        ChildEntity entity = new ChildEntity();
        entity.setName("child");

        BeforeConvertEvent<Object> event = new BeforeConvertEvent<>(
            entity,
            "test_collection"
        );
        listener.onBeforeConvert(event);

        assertNotNull(entity.getId());
        assertTrue(entity.getId() > 0);
    }

    // ----------------------------------------------------------------

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class EntityWithMongoId {

        @Id
        @MongoId(type = MongoId.IdType.ASSIGN_ID)
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class EntityWithUuid {

        @Id
        @MongoId(type = MongoId.IdType.ASSIGN_UUID)
        private String id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class EntityWithBusinessId {

        @Id
        private String id;
        @BusinessId
        private String orderNo;
        private Double amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class EntityWithMultipleBusinessId {

        @Id
        private String id;
        @BusinessId
        private String orderNo;
        @BusinessId
        private String tradeNo;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    static class BaseEntity {

        @Id
        @MongoId(type = MongoId.IdType.ASSIGN_ID)
        private Long id;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    static class ChildEntity extends BaseEntity {

        private String name;
    }
}
