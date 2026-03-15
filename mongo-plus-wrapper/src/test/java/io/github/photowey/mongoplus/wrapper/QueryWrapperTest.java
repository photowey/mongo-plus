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
import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.dsl.support.QueryDSL;
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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QueryWrapperTest - Tests for QueryWrapper.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QueryWrapperTest")
class QueryWrapperTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = 4022003016897913153L;

        private String name;
        private Integer age;
        private String email;
        private String status;
    }

    static final class UserColumns {

        private static final FieldMetadata<String> NAME = FieldMetadata.<String>builder()
            .name("name")
            .column("name")
            .path("name")
            .type(String.class)
            .entityType(User.class)
            .build();
        private static final FieldMetadata<Integer> AGE = FieldMetadata.<Integer>builder()
            .name("age")
            .column("age")
            .path("age")
            .type(Integer.class)
            .entityType(User.class)
            .build();
        private static final FieldMetadata<String> EMAIL = FieldMetadata.<String>builder()
            .name("email")
            .column("email")
            .path("email")
            .type(String.class)
            .entityType(User.class)
            .build();

        private UserColumns() {
        }
    }

    @Test
    @DisplayName("Given field and value When eq Then adds EQ condition")
    @Story("Add eq condition")
    void givenFieldAndValue_whenEq_thenAddsEqCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("name", wrapper.getConditions().get(0).getField());
        assertEquals(Operator.EQ, wrapper.getConditions().get(0).getOperator());
        assertEquals("Tom", wrapper.getConditions().get(0).getValue());
    }

    @Test
    @DisplayName("Given field and value When ne Then adds NE condition")
    @Story("Add ne condition")
    void givenFieldAndValue_whenNe_thenAddsNeCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("status", "deleted");

        assertEquals(1, wrapper.getConditions().size());
        assertEquals("status", wrapper.getConditions().get(0).getField());
        assertEquals(Operator.NE, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field When gt and lt Then adds GT and LT conditions")
    @Story("Add gt and lt conditions")
    void givenField_whenGtAndLt_thenAddsGtAndLtConditions() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.gt("age", 18).lt("age", 60);

        assertEquals(2, wrapper.getConditions().size());
        assertEquals(Operator.GT, wrapper.getConditions().get(0).getOperator());
        assertEquals(Operator.LT, wrapper.getConditions().get(1).getOperator());
    }

    @Test
    @DisplayName("Given field and range When between Then adds BETWEEN condition")
    @Story("Add between condition")
    void givenFieldAndRange_whenBetween_thenAddsBetweenCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.between("age", 18, 60);

        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.BETWEEN, wrapper.getConditions().get(0).getOperator());
        assertEquals(18, wrapper.getConditions().get(0).getValue());
        assertEquals(60, wrapper.getConditions().get(0).getSecondValue());
    }

    @Test
    @DisplayName("Given field and list When in Then adds IN condition")
    @Story("Add in condition")
    void givenFieldAndList_whenIn_thenAddsInCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("status", Arrays.asList("active", "pending"));

        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.IN, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and value When like Then adds LIKE condition")
    @Story("Add like condition")
    void givenFieldAndValue_whenLike_thenAddsLikeCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name", "Tom");

        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.LIKE, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field When isNull Then adds IS_NULL condition")
    @Story("Add isNull condition")
    void givenField_whenIsNull_thenAddsIsNullCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.isNull("email");

        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.IS_NULL, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given fields When orderByAsc and orderByDesc Then adds two sorts")
    @Story("Add order by asc and desc")
    void givenFields_whenOrderByAscAndOrderByDesc_thenAddsTwoSorts() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("age").orderByDesc("name");

        assertEquals(2, wrapper.getSorts().size());
        assertEquals("age", wrapper.getSorts().get(0).getField());
        assertEquals("name", wrapper.getSorts().get(1).getField());
    }

    @Test
    @DisplayName("Given page and size When page Then sets skip and limit")
    @Story("Set skip and limit for pagination")
    void givenPageAndSize_whenPage_thenSetsSkipAndLimit() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.page(2, 10);

        assertEquals(10L, wrapper.getSkip());
        assertEquals(10L, wrapper.getLimit());
    }

    @Test
    @DisplayName("Given QueryWrapper as QueryDSL When shaping query Then projection sort and pagination are stored")
    @Story("QueryWrapper exposes query shaping via QueryDSL")
    void givenQueryWrapperAsQueryDsl_whenShapingQuery_thenProjectionSortAndPaginationAreStored() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        QueryDSL<User, AbstractWrapper<User>> queryDsl = wrapper;

        queryDsl.select("name", "age")
            .orderByDesc("age")
            .paginate(2, 5);

        assertEquals(2, wrapper.getProjections().size());
        assertEquals(1, wrapper.getSorts().size());
        assertEquals("age", wrapper.getSorts().get(0).getField());
        assertEquals(5L, wrapper.getSkip());
        assertEquals(5L, wrapper.getLimit());
    }

    @Test
    @DisplayName("Given QueryWrapper as QueryDSL When shaping query with FieldMetadata Then metadata defaults are applied")
    @Story("QueryDSL metadata defaults shape wrapper state")
    void givenQueryWrapperAsQueryDsl_whenShapingQueryWithFieldMetadata_thenMetadataDefaultsAreApplied() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        QueryDSL<User, AbstractWrapper<User>> queryDsl = wrapper;

        queryDsl.select(UserColumns.NAME, UserColumns.AGE)
            .exclude(UserColumns.EMAIL)
            .orderByAsc(UserColumns.AGE)
            .orderBy(false, UserColumns.NAME)
            .paginate(3L, 7L);

        assertTrue(wrapper.getProjections().contains("name"));
        assertTrue(wrapper.getProjections().contains("age"));
        assertTrue(wrapper.getProjections().contains("-email"));
        assertEquals(2, wrapper.getSorts().size());
        assertEquals("age", wrapper.getSorts().get(0).getField());
        assertEquals("name", wrapper.getSorts().get(1).getField());
        assertEquals(14L, wrapper.getSkip());
        assertEquals(7L, wrapper.getLimit());
    }

    @Test
    @DisplayName("Given fields When select Then adds projections")
    @Story("Add select projections")
    void givenFields_whenSelect_thenAddsProjections() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("name", "age");

        assertEquals(2, wrapper.getProjections().size());
        assertTrue(wrapper.getProjections().contains("name"));
        assertTrue(wrapper.getProjections().contains("age"));
    }

    @Test
    @DisplayName("Given condition When and with nested Then adds two conditions")
    @Story("Add AND with nested conditions")
    void givenCondition_whenAndWithNested_thenAddsTwoConditions() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "active");
        wrapper.and(w -> w.gt("age", 18).lt("age", 60));

        assertEquals(2, wrapper.getConditions().size());
        assertNotNull(wrapper.getConditions().get(1).getNestedWrapper());
    }

    @Test
    @DisplayName("Given condition When or with nested Then adds two conditions")
    @Story("Add OR with nested conditions")
    void givenCondition_whenOrWithNested_thenAddsTwoConditions() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "active");
        wrapper.or(w -> w.eq("status", "pending"));

        assertEquals(2, wrapper.getConditions().size());
        assertNotNull(wrapper.getConditions().get(1).getNestedWrapper());
    }

    @Test
    @DisplayName("Given wrapper with eq orderBy When clone Then returns copy with same size")
    @Story("Clone wrapper with eq and orderBy")
    void givenWrapperWithEqOrderBy_whenClone_thenReturnsCopyWithSameSize() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom").orderByAsc("age");

        QueryWrapper<User> cloned = wrapper.clone();

        assertEquals(wrapper.getConditions().size(), cloned.getConditions().size());
        assertEquals(wrapper.getSorts().size(), cloned.getSorts().size());
    }

    @Test
    @DisplayName("Given field When gte and lte Then adds GTE and LTE conditions")
    @Story("Add gte and lte conditions")
    void givenField_whenGteAndLte_thenAddsGteAndLteConditions() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.gte("age", 18).lte("age", 60);
        assertEquals(2, wrapper.getConditions().size());
        assertEquals(Operator.GTE, wrapper.getConditions().get(0).getOperator());
        assertEquals(Operator.LTE, wrapper.getConditions().get(1).getOperator());
    }

    @Test
    @DisplayName("Given fields and lists When notIn and nin Then adds two conditions")
    @Story("Add notIn and nin conditions")
    void givenFieldsAndLists_whenNotInAndNin_thenAddsTwoConditions() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.notIn("status", Arrays.asList("deleted", "archived"));
        assertEquals(1, wrapper.getConditions().size());
        wrapper.nin("name", Arrays.asList("x", "y"));
        assertEquals(2, wrapper.getConditions().size());
    }

    @Test
    @DisplayName("Given fields When likeLeft and likeRight Then adds two conditions")
    @Story("Add likeLeft and likeRight conditions")
    void givenFields_whenLikeLeftAndLikeRight_thenAddsTwoConditions() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.likeLeft("name", "Tom").likeRight("email", "@example.com");
        assertEquals(2, wrapper.getConditions().size());
        assertEquals(Operator.LIKE_LEFT, wrapper.getConditions().get(0).getOperator());
        assertEquals(Operator.LIKE_RIGHT, wrapper.getConditions().get(1).getOperator());
    }

    @Test
    @DisplayName("Given field and pattern When regex Then adds REGEX condition")
    @Story("Add regex condition")
    void givenFieldAndPattern_whenRegex_thenAddsRegexCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.regex("name", ".*Tom.*");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.REGEX, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field When isNotNull Then adds IS_NOT_NULL condition")
    @Story("Add isNotNull condition")
    void givenField_whenIsNotNull_thenAddsIsNotNullCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.isNotNull("email");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.IS_NOT_NULL, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and boolean When exists Then adds EXISTS condition")
    @Story("Add exists condition")
    void givenFieldAndBoolean_whenExists_thenAddsExistsCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.exists("email", true);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.EXISTS, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and list When all Then adds ALL condition")
    @Story("Add all condition")
    void givenFieldAndList_whenAll_thenAddsAllCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.all("tags", Arrays.asList("a", "b"));
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.ALL, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given field and size When size Then adds SIZE condition")
    @Story("Add size condition")
    void givenFieldAndSize_whenSize_thenAddsSizeCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.size("items", 3);
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.SIZE, wrapper.getConditions().get(0).getOperator());
    }

    @Test
    @DisplayName("Given fields When exclude Then adds exclusion projections")
    @Story("Add exclusion projections")
    void givenFields_whenExclude_thenAddsExclusionProjections() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.exclude("email", "status");
        assertEquals(2, wrapper.getProjections().size());
        assertTrue(wrapper.getProjections().stream().allMatch(p -> p.startsWith("-")));
    }

    @Test
    @DisplayName("Given skip and limit When skip and limit Then sets skip and limit")
    @Story("Set skip and limit")
    void givenSkipAndLimit_whenSkipAndLimit_thenSetsSkipAndLimit() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.skip(5).limit(20);
        assertEquals(5L, wrapper.getSkip());
        assertEquals(20L, wrapper.getLimit());
    }

    @Test
    @DisplayName("Given fields and asc flag When orderBy Then adds two sorts")
    @Story("Add orderBy with asc flag")
    void givenFieldsAndAscFlag_whenOrderBy_thenAddsTwoSorts() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.orderBy(true, "age").orderBy(false, "name");
        assertEquals(2, wrapper.getSorts().size());
        assertEquals("age", wrapper.getSorts().get(0).getField());
        assertEquals("name", wrapper.getSorts().get(1).getField());
    }

    @Test
    @DisplayName("Given cloned QueryWrapper When nested clone mutates Then original nested state stays isolated")
    @Story("Cloned wrapper isolation from nested mutate")
    void givenClonedQueryWrapper_whenNestedCloneMutates_thenOriginalNestedStateStaysIsolated() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom").and(w -> w.gt("age", 18));

        QueryWrapper<User> cloned = wrapper.clone();
        QueryWrapper<?> originalNested = (QueryWrapper<?>) wrapper.getConditions().get(1).getNestedWrapper();
        QueryWrapper<?> clonedNested = (QueryWrapper<?>) cloned.getConditions().get(1).getNestedWrapper();
        clonedNested.lt("age", 60);

        assertNotSame(wrapper.getAstRoot(), cloned.getAstRoot());
        assertNotSame(originalNested, clonedNested);
        assertEquals(1, originalNested.getConditions().size());
        assertEquals(2, clonedNested.getConditions().size());
    }

    @Test
    @DisplayName("Given cloned QueryWrapper When original and clone diverge Then each keeps its own tail condition")
    @Story("Original and clone diverge keep own tail condition")
    void givenClonedQueryWrapper_whenOriginalAndCloneDiverge_thenEachKeepsItsOwnTailCondition() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");

        QueryWrapper<User> cloned = wrapper.clone();
        wrapper.lt("age", 60);
        cloned.eq("status", "active");

        assertEquals(2, wrapper.getConditions().size());
        assertEquals(2, cloned.getConditions().size());
        assertEquals(Operator.LT, wrapper.getConditions().get(1).getOperator());
        assertEquals(Operator.EQ, cloned.getConditions().get(1).getOperator());
        assertEquals("status", cloned.getConditions().get(1).getField());
    }

    @Test
    @DisplayName("Given field and varargs When in Then adds IN condition with collection")
    @Story("Add in condition with varargs collection")
    void givenFieldAndVarargs_whenIn_thenAddsInConditionWithCollection() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("status", "active", "pending");
        assertEquals(1, wrapper.getConditions().size());
        assertEquals(Operator.IN, wrapper.getConditions().get(0).getOperator());
        assertEquals(2, ((Collection<?>) wrapper.getConditions().get(0).getValue()).size());
    }
}
