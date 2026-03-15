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
package io.github.photowey.mongoplus.wrapper;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FieldMetadataWrapperSupportTest - Tests wrapper APIs that consume generated-style field metadata.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("FieldMetadataWrapperSupportTest")
class FieldMetadataWrapperSupportTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = 1L;

        private Long id;
        private String userName;
        private Integer age;
        private Address address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Address implements Serializable {

        private static final long serialVersionUID = 1L;

        private String city;
    }

    static final class UserColumns {

        static final FieldMetadata<Long> ID = FieldMetadata.<Long>builder()
            .name("id")
            .column("_id")
            .path("_id")
            .type(Long.class)
            .entityType(User.class)
            .id(true)
            .build();

        static final FieldMetadata<String> USER_NAME = FieldMetadata.<String>builder()
            .name("userName")
            .column("user_name")
            .path("user_name")
            .type(String.class)
            .entityType(User.class)
            .build();

        static final FieldMetadata<Integer> AGE = FieldMetadata.<Integer>builder()
            .name("age")
            .column("age")
            .path("age")
            .type(Integer.class)
            .entityType(User.class)
            .build();

        static final FieldMetadata<Address> ADDRESS = FieldMetadata.<Address>builder()
            .name("address")
            .column("address")
            .path("address")
            .type(Address.class)
            .entityType(User.class)
            .build();

        static final FieldMetadata<String> ADDRESS_CITY = FieldMetadata.<String>builder()
            .name("city")
            .column("city")
            .path("address.city")
            .type(String.class)
            .entityType(User.class)
            .parent(ADDRESS)
            .build();

        private UserColumns() {
        }
    }

    @Test
    @DisplayName("Given field metadata When building query conditions Then wrapper uses metadata paths")
    @Story("Build query conditions using field metadata paths")
    void givenFieldMetadata_whenBuildingQueryConditions_thenWrapperUsesMetadataPaths() {
        // Given
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        // When
        wrapper.eq(UserColumns.USER_NAME, "tom");
        wrapper.gt(UserColumns.AGE, 18);
        wrapper.isNotNull(UserColumns.ADDRESS_CITY);

        // Then
        assertEquals(3, wrapper.getConditions().size());
        assertEquals("user_name", wrapper.getConditions().get(0).getField());
        assertEquals("age", wrapper.getConditions().get(1).getField());
        assertEquals("address.city", wrapper.getConditions().get(2).getField());
    }

    @Test
    @DisplayName("Given field metadata When selecting sorting and excluding Then wrapper stores metadata paths")
    @Story("Select sort and exclude using metadata paths")
    void givenFieldMetadata_whenSelectingSortingAndExcluding_thenWrapperStoresMetadataPaths() {
        // Given
        QueryWrapper<User> wrapper = new QueryWrapper<>();

        // When
        wrapper.select(UserColumns.USER_NAME, UserColumns.ADDRESS_CITY);
        wrapper.exclude(UserColumns.ID);
        wrapper.orderByAsc(UserColumns.USER_NAME);
        wrapper.orderByDesc(UserColumns.ADDRESS_CITY);

        // Then
        assertEquals(3, wrapper.getProjections().size());
        assertTrue(wrapper.getProjections().contains("user_name"));
        assertTrue(wrapper.getProjections().contains("address.city"));
        assertTrue(wrapper.getProjections().contains("-_id"));
        assertEquals("user_name", wrapper.getSorts().get(0).getField());
        assertEquals("address.city", wrapper.getSorts().get(1).getField());
    }

    @Test
    @DisplayName("Given field metadata When applying updates Then update wrapper uses metadata paths")
    @Story("Apply updates using field metadata paths")
    void givenFieldMetadata_whenApplyingUpdates_thenUpdateWrapperUsesMetadataPaths() {
        // Given
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();

        // When
        wrapper.set(UserColumns.USER_NAME, "neo");
        wrapper.inc(UserColumns.AGE, 1);
        wrapper.setNull(UserColumns.ADDRESS_CITY);

        // Then
        assertEquals("neo", wrapper.getSetValues().get("user_name"));
        assertEquals(1, wrapper.getIncValues().get("age"));
        assertTrue(wrapper.getSetValues().containsKey("address.city"));
        assertNull(wrapper.getSetValues().get("address.city"));
    }
}
