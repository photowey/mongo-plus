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

import io.github.photowey.mongoplus.wrapper.core.util.Wrappers;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LambdaQueryWrapperTest - Tests for LambdaQueryWrapper.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("LambdaQueryWrapperTest")
class LambdaQueryWrapperTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = 2003351014524800771L;

        private String name;
        private Integer age;
        private String email;
        private Boolean active;
    }

    @Test
    @DisplayName("Given field and value When eq Then adds condition with resolved field name")
    @Story("Add equality condition with resolved field name")
    void givenFieldAndValue_whenEq_thenAddsConditionWithResolvedFieldName() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom");

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field and value When gt Then adds condition")
    @Story("Add gt condition")
    void givenFieldAndValue_whenGt_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(User::getAge, 18);

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("age", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field and range When between Then adds one condition")
    @Story("Add between condition")
    void givenFieldAndRange_whenBetween_thenAddsOneCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(User::getAge, 18, 60);

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("age", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field and list When in Then adds condition")
    @Story("Add in condition")
    void givenFieldAndList_whenIn_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(User::getName, Arrays.asList("Tom", "Jerry"));

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field and value When like Then adds condition")
    @Story("Add like condition")
    void givenFieldAndValue_whenLike_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(User::getName, "Tom");

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field When isNull Then adds condition")
    @Story("Add isNull condition")
    void givenField_whenIsNull_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(User::getEmail);

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("email", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field When orderByAsc Then adds sort")
    @Story("Add order by asc")
    void givenField_whenOrderByAsc_thenAddsSort() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(User::getAge);

        assertEquals(1, wrapper.getSorts().size());
        assertEquals("age", wrapper.getSorts().get(0).getField());
    }

    @Test
    @DisplayName("Given field When orderByDesc Then adds sort")
    @Story("Add order by desc")
    void givenField_whenOrderByDesc_thenAddsSort() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getName);

        assertEquals(1, wrapper.getSorts().size());
        assertEquals("name", wrapper.getSorts().get(0).getField());
    }

    @Test
    @DisplayName("Given getters When select Then adds projections")
    @Story("Add projections from getters")
    void givenGetters_whenSelect_thenAddsProjections() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(User::getName, User::getAge);

        assertEquals(2, wrapper.getProjections().size());
        assertTrue(wrapper.getProjections().contains("name"));
        assertTrue(wrapper.getProjections().contains("age"));
    }

    @Test
    @DisplayName("Given condition When and with nested Then adds two conditions")
    @Story("Add AND with nested condition")
    void givenCondition_whenAndWithNested_thenAddsTwoConditions() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom")
            .and(w -> w.gt(User::getAge, 18));

        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given condition When or with nested Then adds two conditions")
    @Story("Add OR with nested condition")
    void givenCondition_whenOrWithNested_thenAddsTwoConditions() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom")
            .or(w -> w.eq(User::getName, "Jerry"));

        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given chained eq gt lt like orderBy limit When build Then all applied")
    @Story("Chained eq gt lt like orderBy limit all applied")
    void givenChainedEqGtLtLikeOrderByLimit_whenBuild_thenAllApplied() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom")
            .gt(User::getAge, 18)
            .lt(User::getAge, 60)
            .like(User::getEmail, "@gmail.com")
            .orderByDesc(User::getAge)
            .limit(10);

        assertEquals(4, wrapper.getConditions().size());
        assertEquals(1, wrapper.getSorts().size());
        assertEquals(10L, wrapper.getLimit());
    }

    @Test
    @DisplayName("Given field and value When ne Then adds condition")
    @Story("Add ne condition")
    void givenFieldAndValue_whenNe_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(User::getActive, false);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("active", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field When lt and lte Then adds two conditions")
    @Story("Add lt and lte conditions")
    void givenField_whenLtAndLte_thenAddsTwoConditions() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(User::getAge, 60);
        assertEquals(1, wrapper.getConditions().size());
        wrapper.lte(User::getAge, 60);
        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given field and list When nin Then adds condition")
    @Story("Add nin condition")
    void givenFieldAndList_whenNin_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.nin(User::getName, Arrays.asList("a", "b"));
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given fields When likeLeft and likeRight Then adds two conditions")
    @Story("Add likeLeft and likeRight conditions")
    void givenFields_whenLikeLeftAndLikeRight_thenAddsTwoConditions() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeLeft(User::getName, "Tom");
        wrapper.likeRight(User::getEmail, "@example.com");
        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given field and pattern When regex Then adds condition")
    @Story("Add regex condition")
    void givenFieldAndPattern_whenRegex_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.regex(User::getName, ".*Tom.*");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field When isNotNull Then adds condition")
    @Story("Add isNotNull condition")
    void givenField_whenIsNotNull_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(User::getEmail);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("email", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field and boolean When exists Then adds condition")
    @Story("Add exists condition")
    void givenFieldAndBoolean_whenExists_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.exists(User::getEmail, true);
        assertEquals(1, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given field and list When all Then adds condition")
    @Story("Add all condition")
    void givenFieldAndList_whenAll_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.all(User::getName, Arrays.asList("a", "b"));
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }

    @Test
    @DisplayName("Given field and size When size Then adds condition")
    @Story("Add size condition")
    void givenFieldAndSize_whenSize_thenAddsCondition() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.size(User::getName, 2);
        assertEquals(1, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given fields and asc flag When orderBy Then adds sorts")
    @Story("Add orderBy with asc flag")
    void givenFieldsAndAscFlag_whenOrderBy_thenAddsSorts() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderBy(User::getAge, true).orderBy(User::getName, false);
        assertEquals(2, wrapper.getSorts().size());
        assertEquals("age", wrapper.getSorts().get(0).getField());
        assertEquals("name", wrapper.getSorts().get(1).getField());
    }

    @Test
    @DisplayName("Given getter When exclude Then adds exclusion projection")
    @Story("Add exclusion projection from getter")
    void givenGetter_whenExclude_thenAddsExclusionProjection() {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.exclude(User::getEmail);
        assertEquals(1, wrapper.getProjections().size());
        assertTrue(wrapper.getProjections().get(0).startsWith("-"));
    }

    @Test
    @DisplayName("Given page and size When page Then sets skip and limit")
    @Story("Set skip and limit for page")
    void givenPageAndSize_whenPage_thenSetsSkipAndLimit() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.page(2, 10);
        assertEquals(10L, wrapper.getSkip());
        assertEquals(10L, wrapper.getLimit());
    }

    @Test
    @DisplayName("Given wrapper with conditions When clone Then returns copy with same structure")
    @Story("Clone wrapper returns copy with same structure")
    void givenWrapperWithConditions_whenClone_thenReturnsCopyWithSameStructure() {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.eq(User::getName, "Tom").orderByAsc(User::getAge);
        LambdaQueryWrapper<User> cloned = wrapper.clone();
        assertNotSame(wrapper, cloned);
        assertEquals(wrapper.getConditions().size(), cloned.getConditions().size());
        assertEquals(wrapper.getSorts().size(), cloned.getSorts().size());
    }

    @Test
    @DisplayName("Given cloned LambdaQueryWrapper When nested clone mutates Then original nested state stays isolated")
    @Story("Cloned wrapper isolation from nested mutate")
    void givenClonedLambdaQueryWrapper_whenNestedCloneMutates_thenOriginalNestedStateStaysIsolated() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom").and(w -> w.gt(User::getAge, 18));

        LambdaQueryWrapper<User> cloned = wrapper.clone();
        AbstractWrapper<?> originalNested = wrapper.getConditions().get(1).getNestedWrapper();
        AbstractWrapper<?> clonedNested = cloned.getConditions().get(1).getNestedWrapper();
        clonedNested.lt("age", 60);

        assertNotSame(wrapper.getAstRoot(), cloned.getAstRoot());
        assertNotSame(originalNested, clonedNested);
        assertEquals(1, originalNested.getConditions().size());
        assertEquals(2, clonedNested.getConditions().size());
    }

    @Test
    @DisplayName("Given field and varargs When in Then adds condition and returns self")
    @Story("Add in condition with varargs returns self")
    void givenFieldAndVarargs_whenIn_thenAddsConditionAndReturnsSelf() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> chained = wrapper.in(User::getName, "Tom", "Jerry");
        assertSame(wrapper, chained);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
    }
}
