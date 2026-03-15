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
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UpdateWrapperTest - Tests for UpdateWrapper.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("UpdateWrapperTest")
class UpdateWrapperTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = 8536306935856697343L;

        private String name;
        private Integer age;
        private String email;
    }

    @Test
    @DisplayName("Given field and value When set Then adds to setValues")
    @Story("Add set values")
    void givenFieldAndValue_whenSet_thenAddsToSetValues() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("name", "Tom").set("age", 25);
        assertEquals(2, wrapper.getSetValues().size());
        assertEquals("Tom", wrapper.getSetValues().get("name"));
        assertEquals(25, wrapper.getSetValues().get("age"));
    }

    @Test
    @DisplayName("Given field When setNull Then adds null to setValues")
    @Story("Add null to set values")
    void givenField_whenSetNull_thenAddsNullToSetValues() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.setNull("email");
        assertEquals(1, wrapper.getSetValues().size());
        assertNull(wrapper.getSetValues().get("email"));
    }

    @Test
    @DisplayName("Given field and delta When inc Then adds to incValues")
    @Story("Add inc delta")
    void givenFieldAndDelta_whenInc_thenAddsToIncValues() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.inc("count", 1).inc("score", 10L);
        assertEquals(2, wrapper.getIncValues().size());
        assertEquals(1, wrapper.getIncValues().get("count"));
        assertEquals(10L, wrapper.getIncValues().get("score"));
    }

    @Test
    @DisplayName("Given condition When and with nested Then adds two conditions")
    @Story("Add AND with nested conditions")
    void givenCondition_whenAndWithNested_thenAddsTwoConditions() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("status", "active").and(w -> w.gt("age", 18));
        assertEquals(2, wrapper.getConditions().size());
        assertNotNull(wrapper.getConditions().get(1).getNestedWrapper());
    }

    @Test
    @DisplayName("Given condition When or with nested Then adds two conditions")
    @Story("Add OR with nested conditions")
    void givenCondition_whenOrWithNested_thenAddsTwoConditions() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("status", "active").or(w -> w.eq("status", "pending"));
        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given wrapper When set or inc Then hasUpdates returns true")
    @Story("Has updates when set or inc")
    void givenWrapper_whenSetOrInc_thenHasUpdatesReturnsTrue() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        assertFalse(wrapper.hasUpdates());
        wrapper.set("name", "Tom");
        assertTrue(wrapper.hasUpdates());
        UpdateWrapper<User> wrapper2 = new UpdateWrapper<>();
        wrapper2.inc("count", 1);
        assertTrue(wrapper2.hasUpdates());
    }

    @Test
    @DisplayName("Given set and inc When access Then setValues and incValues return correct maps")
    @Story("Access set and inc values maps")
    void givenSetAndInc_whenAccess_thenSetValuesAndIncValuesReturnCorrectMaps() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("a", 1).inc("b", 2);
        assertEquals(1, wrapper.setValues().size());
        assertEquals(1, wrapper.incValues().size());
        assertEquals(1, wrapper.setValues().get("a"));
        assertEquals(2, wrapper.incValues().get("b"));
    }

    @Test
    @DisplayName("Given wrapper with set inc eq When clone Then returns copy with same structure")
    @Story("Clone returns copy with same structure")
    void givenWrapperWithSetIncEq_whenClone_thenReturnsCopyWithSameStructure() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("name", "Tom").inc("age", 1).eq("status", "active");
        UpdateWrapper<User> cloned = wrapper.clone();
        assertNotSame(wrapper, cloned);
        assertEquals(wrapper.getSetValues().size(), cloned.getSetValues().size());
        assertEquals(wrapper.getIncValues().size(), cloned.getIncValues().size());
        assertEquals(wrapper.getConditions().size(), cloned.getConditions().size());
    }

    @Test
    @DisplayName("Given between and set When build Then condition and setValues present")
    @Story("Build with between and set")
    void givenBetweenAndSet_whenBuild_thenConditionAndSetValuesPresent() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.between("age", 18, 60);
        wrapper.set("status", "active");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.BETWEEN, wrapper.getConditions().get(0).getOperator());
        assertEquals(1, wrapper.getSetValues().size());
    }

    @Test
    @DisplayName("Given like and set When build Then LIKE condition and set present")
    @Story("Build with like and set")
    void givenLikeAndSet_whenBuild_thenLikeConditionAndSetPresent() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.like("name", "Tom");
        wrapper.set("email", "t@e.com");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.LIKE, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and pattern When regex Then adds REGEX condition")
    @Story("Add regex condition")
    void givenFieldAndPattern_whenRegex_thenAddsRegexCondition() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.regex("name", ".*Tom.*");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.REGEX, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given exists and set When build Then EXISTS condition present")
    @Story("Build with exists and set")
    void givenExistsAndSet_whenBuild_thenExistsConditionPresent() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.exists("email", true);
        wrapper.set("name", "x");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.EXISTS, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and list When all Then adds ALL condition")
    @Story("Add all condition")
    void givenFieldAndList_whenAll_thenAddsAllCondition() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.all("tags", Arrays.asList("a", "b"));
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.ALL, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and size When size Then adds SIZE condition")
    @Story("Add size condition")
    void givenFieldAndSize_whenSize_thenAddsSizeCondition() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.size("items", 3);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.SIZE, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given cloned UpdateWrapper When nested clone mutates Then original nested state stays isolated")
    @Story("Cloned wrapper nested mutation isolates original")
    void givenClonedUpdateWrapper_whenNestedCloneMutates_thenOriginalNestedStateStaysIsolated() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("name", "Tom").and(w -> w.gt("age", 18));

        UpdateWrapper<User> cloned = wrapper.clone();
        UpdateWrapper<?> originalNested = (UpdateWrapper<?>) wrapper.getConditions().get(0).getNestedWrapper();
        UpdateWrapper<?> clonedNested = (UpdateWrapper<?>) cloned.getConditions().get(0).getNestedWrapper();
        clonedNested.lt("age", 60);
        cloned.set("email", "clone@example.com");

        assertNotSame(wrapper.getAstRoot(), cloned.getAstRoot());
        assertNotSame(originalNested, clonedNested);
        assertEquals(1, originalNested.getConditions().size());
        assertEquals(2, clonedNested.getConditions().size());
        assertFalse(wrapper.getSetValues().containsKey("email"));
    }

    @Test
    @DisplayName("Given cloned UpdateWrapper When original and clone diverge Then each keeps its own state")
    @Story("Original and clone diverge keep own state")
    void givenClonedUpdateWrapper_whenOriginalAndCloneDiverge_thenEachKeepsItsOwnState() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("name", "Tom").eq("status", "active");

        UpdateWrapper<User> cloned = wrapper.clone();
        wrapper.inc("age", 1);
        cloned.set("email", "clone@example.com");

        assertTrue(wrapper.getIncValues().containsKey("age"));
        assertFalse(wrapper.getSetValues().containsKey("email"));
        assertTrue(cloned.getSetValues().containsKey("email"));
        assertFalse(cloned.getIncValues().containsKey("age"));
    }

    @Test
    @DisplayName("Given field and list When notIn Then adds NIN condition")
    @Story("Add nin condition for field and list")
    void givenFieldAndList_whenNotIn_thenAddsNinCondition() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.notIn("status", Arrays.asList("deleted", "archived"));
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.NIN, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given UpdateWrapper When paginate with long values Then skip and limit use long state")
    @Story("UpdateWrapper paginate stores long pagination state")
    void givenUpdateWrapper_whenPaginateWithLongValues_thenSkipAndLimitUseLongState() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();

        wrapper.paginate(4L, 6L);

        assertEquals(18L, wrapper.getSkip());
        assertEquals(6L, wrapper.getLimit());
    }
}

