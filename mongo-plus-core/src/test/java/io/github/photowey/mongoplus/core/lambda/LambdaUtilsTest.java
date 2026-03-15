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
package io.github.photowey.mongoplus.core.lambda;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LambdaUtilsTest - Tests for LambdaUtils.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("LambdaUtilsTest")
class LambdaUtilsTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = -8978984902399032992L;

        private String name;
        private Integer age;
        private boolean active;
    }

    @Test
    @DisplayName("Given getter User::getName When resolve Then returns name")
    @Story("Resolve getUserName getter to name")
    void givenGetterUserGetName_whenResolve_thenReturnsName() {
        String fieldName = LambdaUtils.resolve(User::getName);
        assertEquals("name", fieldName);
    }

    @Test
    @DisplayName("Given getter User::getAge When resolve Then returns age")
    @Story("Resolve getUserAge getter to age")
    void givenGetterUserGetAge_whenResolve_thenReturnsAge() {
        String fieldName = LambdaUtils.resolve(User::getAge);
        assertEquals("age", fieldName);
    }

    @Test
    @DisplayName("Given boolean getter User::isActive When resolve Then returns active")
    @Story("Resolve isActive boolean getter to active")
    void givenBooleanGetterUserIsActive_whenResolve_thenReturnsActive() {
        String fieldName = LambdaUtils.resolve(User::isActive);
        assertEquals("active", fieldName);
    }

    @Test
    @DisplayName("Given same getter resolved twice When resolve Then returns same field name")
    @Story("Same getter resolves to same field name twice")
    void givenSameGetterResolvedTwice_whenResolve_thenReturnsSameFieldName() {
        String fieldName1 = LambdaUtils.resolve(User::getName);
        assertEquals("name", fieldName1);

        String fieldName2 = LambdaUtils.resolve(User::getName);
        assertEquals("name", fieldName2);

        assertEquals(fieldName1, fieldName2);
    }

    @Test
    @DisplayName("Given getter When resolveQuietly Then returns field name without throwing")
    @Story("Resolve quietly returns field name without throwing")
    void givenGetter_whenResolveQuietly_thenReturnsFieldNameWithoutThrowing() {
        String fieldName = LambdaUtils.resolveQuietly(User::getName);
        assertEquals("name", fieldName);
    }

    @Test
    @DisplayName("Given class When preCacheClass Then getCachedFieldName returns method to field mapping")
    @Story("Pre-cache class returns method-to-field mapping")
    void givenClass_whenPreCacheClass_thenGetCachedFieldNameReturnsMethodToFieldMapping() {
        LambdaUtils.clearCache();
        LambdaUtils.preCacheClass(User.class);

        assertEquals("name", LambdaUtils.getCachedFieldName(User.class, "getName"));
        assertEquals("age", LambdaUtils.getCachedFieldName(User.class, "getAge"));
        assertEquals("active", LambdaUtils.getCachedFieldName(User.class, "isActive"));
    }

    @Test
    @DisplayName("Given cache cleared When getCacheStats Then returns stats string with zero count")
    @Story("Cache stats show zero count after clear")
    void givenCacheCleared_whenGetCacheStats_thenReturnsStatsStringWithZeroCount() {
        LambdaUtils.clearCache();
        String stats = LambdaUtils.getCacheStats();
        assertNotNull(stats);
        assertTrue(stats.contains("LambdaCache: 0"));
    }
}
