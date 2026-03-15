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
package io.github.photowey.mongoplus.mapper.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.mapper.MongoMapper;
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

@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("MappersTest")
class MappersTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        private String id;
        private String name;
    }

    @Test
    @DisplayName("Given interface extending MongoMapper When resolve Then returns entity class")
    @Story("Resolve entity class from MongoMapper-extending interface")
    void givenInterfaceExtendingMongoMapper_whenResolveEntityClass_thenReturns() {
        Class<?> entity = Mappers.resolve(UserMapper.class);
        assertEquals(User.class, entity);
    }

    @Test
    @DisplayName("Given non-interface class When resolve Then returns null")
    @Story("Non-interface class resolves to null")
    void givenNonInterfaceClass_whenResolve_thenReturnsNull() {
        assertNull(Mappers.resolve(User.class));
    }

    @Test
    @DisplayName("Given null When resolve Then returns null")
    @Story("Null input resolves to null")
    void givenNull_whenResolve_thenReturnsNull() {
        assertNull(Mappers.resolve(null));
    }

    @Test
    @DisplayName("Given interface not extending MongoMapper When resolve Then returns null")
    @Story("Interface not extending MongoMapper resolves to null")
    void givenInterfaceNotExtendingMongoMapper_whenResolve_thenReturnsNull() {
        assertNull(Mappers.resolve(OtherInterface.class));
    }

    interface UserMapper extends MongoMapper<User> {
    }

    interface OtherInterface {
    }
}
