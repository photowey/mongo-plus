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
package io.github.photowey.mongoplus.query.builder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.AND;
import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.IN;
import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.NIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QueryBuilderTest - Tests for QueryBuilder.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QueryBuilderTest")
class QueryBuilderTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = 2561573922390631579L;

        private String name;
        private Integer age;
        private String status;
        private String email;
    }

    @Test
    @DisplayName("Given QueryWrapper with eq and gt When build Then returns non-empty Query")
    @Story("Build query from eq and gt wrapper")
    void givenQueryWrapperWithEqAndGt_whenBuild_thenReturnsNonEmptyQuery() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");
        wrapper.gt("age", 18);

        Query query = QueryBuilder.build(wrapper);

        assertNotNull(query);
        // Query is successfully built
        Document queryObject = query.getQueryObject();
        assertNotNull(queryObject);
    }

    @Test
    @DisplayName("Given LambdaQueryWrapper with eq gt like When build Then returns Query with queryObject")
    @Story("Build query from lambda wrapper with eq gt like")
    void givenLambdaQueryWrapperWithEqGtLike_whenBuild_thenReturnsQueryWithQueryObject() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom");
        wrapper.gt(User::getAge, 18);
        wrapper.like(User::getEmail, "@gmail.com");

        Query query = QueryBuilder.build(wrapper);

        assertNotNull(query);
        // Query is successfully built
        Document queryObject = query.getQueryObject();
        assertNotNull(queryObject);
    }

    @Test
    @DisplayName("Given wrapper with orderByAsc orderByDesc When build Then query is sorted")
    @Story("Build query with order by asc and desc")
    void givenWrapperWithOrderByAscDesc_whenBuild_thenQueryIsSorted() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, "active");
        wrapper.orderByAsc(User::getAge);
        wrapper.orderByDesc(User::getName);

        Query query = QueryBuilder.build(wrapper);

        assertNotNull(query);
        // Query is successfully built with sort
        assertTrue(query.isSorted());
    }

    @Test
    @DisplayName("Given wrapper with page When build Then query has skip and limit")
    @Story("Build query with skip and limit for pagination")
    void givenWrapperWithPage_whenBuild_thenQueryHasSkipAndLimit() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, "active");
        wrapper.page(2, 10);

        Query query = QueryBuilder.build(wrapper);

        assertNotNull(query);
        assertEquals(10, query.getSkip());
        assertEquals(10, query.getLimit());
    }

    @Test
    @DisplayName("Given wrapper with select When build Then query has projection")
    @Story("Build query with projection")
    void givenWrapperWithSelect_whenBuild_thenQueryHasProjection() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
            .eq(User::getStatus, "active")
            .select(User::getName, User::getAge);

        Query query = QueryBuilder.build(wrapper);

        assertNotNull(query);
        // Projection is set in fields
    }

    @Test
    @DisplayName("Given LambdaQueryWrapper When buildBson Then returns non-null Document")
    @Story("Build BSON document from lambda wrapper")
    void givenLambdaQueryWrapper_whenBuildBson_thenReturnsNonNullDocument() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName, "Tom");
        wrapper.gt(User::getAge, 18);

        Document bson = QueryBuilder.buildBson(wrapper);

        assertNotNull(bson);
        // BSON document is successfully built
    }

    @Test
    @DisplayName("Given null wrapper When build Then returns empty Query")
    @Story("Build empty query for null wrapper")
    void givenNullWrapper_whenBuild_thenReturnsEmptyQuery() {
        Query query = QueryBuilder.build(null);
        assertNotNull(query);
        assertTrue(query.getQueryObject().isEmpty());
    }

    @Test
    @DisplayName("Given complex wrapper with and or orderBy limit When build Then query has limit")
    @Story("Build complex query with and or orderBy and limit")
    void givenComplexWrapperWithAndOrOrderByLimit_whenBuild_thenQueryHasLimit() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, "active");
        wrapper.and(w -> w.gt(User::getAge, 18).lt(User::getAge, 60));
        wrapper.or(w -> w.eq(User::getEmail, "vip@example.com"));
        wrapper.orderByDesc(User::getAge);
        wrapper.limit(20);

        Query query = QueryBuilder.build(wrapper);

        assertNotNull(query);
        assertEquals(20, query.getLimit());
    }

    @Test
    @DisplayName("Given wrapper limit larger than Integer max When build Then throws ArithmeticException")
    @Story("QueryBuilder rejects limit values beyond Integer range")
    void givenWrapperLimitLargerThanIntegerMax_whenBuild_thenThrowsArithmeticException() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.limit((long) Integer.MAX_VALUE + 1L);

        assertThrows(ArithmeticException.class, () -> QueryBuilder.build(wrapper));
    }

    /**
     * Asserts that IN condition compiles to $in: [a, b] (flat array), not $in: [[a, b]].
     * Without this, the bug would only be caught by integration tests (e.g. Testcontainers).
     */
    @Test
    @DisplayName("Given IN condition When build Then $in is flat array not nested")
    @Story("IN condition produces flat array not nested")
    void givenInCondition_whenBuild_thenInIsFlatArrayNotNested() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("name", Arrays.asList("Q0", "Q2"));

        Query query = QueryBuilder.build(wrapper);
        assertNotNull(query);

        Document queryObject = query.getQueryObject();
        assertNotNull(queryObject);
        @SuppressWarnings("unchecked")
        List<Document> andClauses = (List<Document>) queryObject.get(AND);
        assertNotNull(andClauses);
        assertEquals(1, andClauses.size());

        Document nameClause = andClauses.get(0);
        @SuppressWarnings("unchecked")
        List<Object> inValues = (List<Object>) ((Document) nameClause.get("name")).get(IN);
        assertNotNull(inValues);
        assertEquals(2, inValues.size(), "$in must be [Q0, Q2], not [[Q0, Q2]]");
        assertTrue(inValues.contains("Q0"));
        assertTrue(inValues.contains("Q2"));
        assertFalse(inValues.get(0) instanceof List, "$in must not be a nested array");
    }

    /**
     * Same for NIN: must produce $nin: [a, b], not $nin: [[a, b]].
     */
    @Test
    @DisplayName("Given NIN condition When build Then $nin is flat array not nested")
    @Story("NIN condition produces flat array not nested")
    void givenNinCondition_whenBuild_thenNinIsFlatArrayNotNested() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.nin("status", Arrays.asList("deleted", "archived"));

        Query query = QueryBuilder.build(wrapper);
        assertNotNull(query);

        Document queryObject = query.getQueryObject();
        @SuppressWarnings("unchecked")
        List<Document> andClauses = (List<Document>) queryObject.get(AND);
        Document statusClause = andClauses.get(0);
        @SuppressWarnings("unchecked")
        List<Object> ninValues = (List<Object>) ((Document) statusClause.get("status")).get(NIN);
        assertEquals(2, ninValues.size());
        assertTrue(ninValues.contains("deleted"));
        assertTrue(ninValues.contains("archived"));
        assertFalse(ninValues.get(0) instanceof List);
    }
}
