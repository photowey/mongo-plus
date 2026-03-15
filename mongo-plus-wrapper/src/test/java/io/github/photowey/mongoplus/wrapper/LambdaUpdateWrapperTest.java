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
 * LambdaUpdateWrapperTest - Tests for LambdaUpdateWrapper.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("LambdaUpdateWrapperTest")
class LambdaUpdateWrapperTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = -1464342923126951681L;

        private String name;
        private Integer age;
        private String email;
    }

    @Test
    @DisplayName("Given getters and values When set Then adds to setValues by field name")
    @Story("Add set values by field name from getters")
    void givenGettersAndValues_whenSet_thenAddsToSetValuesByFieldName() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(User::getName, "Tom").set(User::getAge, 25);
        assertEquals(2, wrapper.getSetValues().size());
        assertEquals("Tom", wrapper.getSetValues().get("name"));
        assertEquals(25, wrapper.getSetValues().get("age"));
    }

    @Test
    @DisplayName("Given getter When setNull Then adds null to setValues")
    @Story("Add null to set values")
    void givenGetter_whenSetNull_thenAddsNullToSetValues() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setNull(User::getEmail);
        assertEquals(1, wrapper.getSetValues().size());
        assertNull(wrapper.getSetValues().get("email"));
    }

    @Test
    @DisplayName("Given getter and delta When inc Then adds to incValues")
    @Story("Add inc delta by getter")
    void givenGetterAndDelta_whenInc_thenAddsToIncValues() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.inc(User::getAge, 1);
        assertEquals(1, wrapper.getIncValues().size());
        assertEquals(1, wrapper.getIncValues().get("age"));
    }

    @Test
    @DisplayName("Given getters and values When eq and ne Then adds two conditions")
    @Story("Add eq and ne conditions")
    void givenGettersAndValues_whenEqAndNe_thenAddsTwoConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getName, "Tom").ne(User::getEmail, null);
        assertEquals(2, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
        assertEquals("email", wrapper.getConditions().get(1).getField());
    }

    @Test
    @DisplayName("Given getter When gt gte lt lte Then adds four conditions")
    @Story("Add gt gte lt lte conditions")
    void givenGetter_whenGtGteLtLte_thenAddsFourConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.gt(User::getAge, 18).gte(User::getAge, 18)
            .lt(User::getAge, 60).lte(User::getAge, 60);
        assertEquals(4, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given getter and value When like Then adds condition")
    @Story("Add like condition")
    void givenGetterAndValue_whenLike_thenAddsCondition() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.like(User::getName, "Tom");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given getters When isNull and isNotNull Then adds two conditions")
    @Story("Add isNull and isNotNull conditions")
    void givenGetters_whenIsNullAndIsNotNull_thenAddsTwoConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.isNull(User::getEmail).isNotNull(User::getName);
        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given wrapper with set eq When clone Then returns copy with same structure")
    @Story("Clone returns copy with same structure")
    void givenWrapperWithSetEq_whenClone_thenReturnsCopyWithSameStructure() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(User::getName, "Tom").eq(User::getAge, 25);
        LambdaUpdateWrapper<User> cloned = wrapper.clone();
        assertNotSame(wrapper, cloned);
        assertEquals(wrapper.getSetValues().size(), cloned.getSetValues().size());
        assertEquals(wrapper.getConditions().size(), cloned.getConditions().size());
    }

    @Test
    @DisplayName("Given wrapper with eq set When clone Then cloned has ast and buildAst")
    @Story("Cloned wrapper has AST and buildAst")
    void givenWrapperWithEqSet_whenClone_thenClonedHasAstAndBuildAst() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getName, "Tom").set(User::getAge, 1);
        LambdaUpdateWrapper<User> cloned = wrapper.clone();
        assertNotNull(cloned.getAstRoot());
        assertNotNull(cloned.getCurrentLogical());
        assertNotNull(cloned.buildAst());
    }

    @Test
    @DisplayName("Given getters and lists When in and notIn Then adds IN and NIN conditions")
    @Story("Add in and nin conditions from getters")
    void givenGettersAndLists_whenInAndNotIn_thenAddsInAndNinConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(User::getName, Arrays.asList("a", "b"));
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.IN, wrapper.getConditions().get(0).getOperator());
        wrapper.notIn(User::getEmail, Arrays.asList("x@y.com"));
        assertEquals(2, wrapper.getConditions().size());
        assertEquals(Operator.NIN, wrapper.getConditions().get(1).getOperator());
    }

    @Test
    @DisplayName("Given getter and range When between Then adds BETWEEN condition")
    @Story("Add between condition")
    void givenGetterAndRange_whenBetween_thenAddsBetweenCondition() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.between(User::getAge, 18, 60);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.BETWEEN, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given getters When likeLeft and likeRight Then adds two conditions")
    @Story("Add likeLeft and likeRight conditions")
    void givenGetters_whenLikeLeftAndLikeRight_thenAddsTwoConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.likeLeft(User::getName, "Tom").likeRight(User::getEmail, "@example.com");
        assertEquals(2, wrapper.getConditions().size());
        assertEquals(Operator.LIKE_LEFT, wrapper.getConditions().get(0).getOperator());
        assertEquals(Operator.LIKE_RIGHT, wrapper.getConditions().get(1).getOperator());
    }

    @Test
    @DisplayName("Given getter and pattern When regex Then adds REGEX condition")
    @Story("Add regex condition")
    void givenGetterAndPattern_whenRegex_thenAddsRegexCondition() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.regex(User::getName, ".*Tom.*");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.REGEX, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given getters When exists all size Then adds EXISTS ALL SIZE conditions")
    @Story("Add exists all size conditions")
    void givenGetters_whenExistsAllSize_thenAddsExistsAllSizeConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.exists(User::getEmail, true);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.EXISTS, wrapper.getConditions().get(0).getOperator());
        wrapper.all(User::getName, Arrays.asList("a", "b"));
        assertEquals(2, wrapper.getConditions().size());
        assertEquals(Operator.ALL, wrapper.getConditions().get(1).getOperator());
        wrapper.size(User::getAge, 2);
        assertEquals(3, wrapper.getConditions().size());
        assertEquals(Operator.SIZE, wrapper.getConditions().get(2).getOperator());
    }

    @Test
    @DisplayName("Given cloned LambdaUpdateWrapper When nested clone mutates Then original nested state stays isolated")
    @Story("Cloned wrapper isolation from nested mutate")
    void givenClonedLambdaUpdateWrapper_whenNestedCloneMutates_thenOriginalNestedStateStaysIsolated() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(User::getName, "Tom")
            .and(w -> w.gt(User::getAge, 18));

        LambdaUpdateWrapper<User> cloned = wrapper.clone();
        AbstractWrapper<?> originalNested = wrapper.getConditions().get(0).getNestedWrapper();
        AbstractWrapper<?> clonedNested = cloned.getConditions().get(0).getNestedWrapper();
        clonedNested.lt("age", 60);
        cloned.set(User::getEmail, "clone@example.com");

        assertNotSame(wrapper.getAstRoot(), cloned.getAstRoot());
        assertNotSame(originalNested, clonedNested);
        assertEquals(1, originalNested.getConditions().size());
        assertEquals(2, clonedNested.getConditions().size());
        assertFalse(wrapper.getSetValues().containsKey("email"));
    }

    @Test
    @DisplayName("Given conditions When and and or with nested Then adds nested conditions")
    @Story("Add AND and OR with nested conditions")
    void givenConditions_whenAndAndOrWithNested_thenAddsNestedConditions() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getName, "Tom")
            .and(w -> w.gt(User::getAge, 18).lt(User::getAge, 60));
        assertEquals(2, wrapper.getConditions().size());
        assertTrue(wrapper.getConditions().get(1).getNestedWrapper() instanceof LambdaUpdateWrapper);
        wrapper.or(w -> w.eq(User::getEmail, "x@y.com"));
        assertEquals(3, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given LambdaUpdateWrapper When paginate with long values Then skip and limit use long state")
    @Story("LambdaUpdateWrapper paginate stores long pagination state")
    void givenLambdaUpdateWrapper_whenPaginateWithLongValues_thenSkipAndLimitUseLongState() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();

        wrapper.paginate(5L, 8L);

        assertEquals(32L, wrapper.getSkip());
        assertEquals(8L, wrapper.getLimit());
    }
}

