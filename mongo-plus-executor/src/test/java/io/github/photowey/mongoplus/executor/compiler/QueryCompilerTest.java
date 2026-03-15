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
package io.github.photowey.mongoplus.executor.compiler;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.executor.compiler.impl.MongoQueryCompiler;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.segment.QuerySegment;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.IN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QueryCompilerTest - Tests for QueryCompiler and legacy QuerySegment compatibility.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QueryCompilerTest")
class QueryCompilerTest {

    @Test
    @DisplayName("Given wrapper When compile Then returns query from primary wrapper contract")
    @Story("Compile wrapper to query document")
    void givenWrapper_whenCompile_thenReturnsQueryFromPrimaryWrapperContract() {
        QueryCompiler compiler = new MongoQueryCompiler();
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");

        Query query = compiler.compile(wrapper);

        assertNotNull(query);
        assertFalse(query.getQueryObject().isEmpty());
        assertTrue(query.getQueryObject().toJson().contains("Tom"));
    }

    @Test
    @DisplayName("Given EQ condition segment When compile Then query object has field value")
    @Story("Compile eq segment to query with field value")
    void givenEqConditionSegment_whenCompile_thenQueryObjectHasFieldValue() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();

        QuerySegment segment = QuerySegment.condition(
            Condition.builder()
                .field("name")
                .operator(Operator.EQ)
                .value("Tom")
                .build()
        );

        Query query = compiler.compile(segment);

        assertNotNull(query);
        Document queryObject = query.getQueryObject();
        assertEquals("Tom", queryObject.getString("name"));
    }

    @Test
    @DisplayName("Given AND segment with conditions When compile Then query not empty")
    @Story("Compile AND segment with conditions")
    void givenAndSegmentWithConditions_whenCompile_thenQueryNotEmpty() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();

        QuerySegment andSegment = QuerySegment.and();
        andSegment.addCondition(Condition.builder()
            .field("age")
            .operator(Operator.GT)
            .value(18)
            .build());
        andSegment.addCondition(Condition.builder()
            .field("status")
            .operator(Operator.EQ)
            .value("active")
            .build());

        Query query = compiler.compile(andSegment);

        assertNotNull(query);
        assertFalse(query.getQueryObject().isEmpty());
    }

    @Test
    @DisplayName("Given OR segment with conditions When compile Then query not empty")
    @Story("Compile OR segment with conditions")
    void givenOrSegmentWithConditions_whenCompile_thenQueryNotEmpty() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();

        QuerySegment orSegment = QuerySegment.or();
        orSegment.addCondition(Condition.builder()
            .field("status")
            .operator(Operator.EQ)
            .value("active")
            .build());
        orSegment.addCondition(Condition.builder()
            .field("status")
            .operator(Operator.EQ)
            .value("pending")
            .build());

        Query query = compiler.compile(orSegment);

        assertNotNull(query);
        assertFalse(query.getQueryObject().isEmpty());
    }

    @Test
    @DisplayName("Given root segment with condition When compile Then query object has field")
    @Story("Compile root segment with condition")
    void givenRootSegmentWithCondition_whenCompile_thenQueryObjectHasField() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();

        QuerySegment root = QuerySegment.root();
        root.addCondition(Condition.builder()
            .field("name")
            .operator(Operator.EQ)
            .value("Tom")
            .build());

        Query query = compiler.compile(root);

        assertNotNull(query);
        assertEquals("Tom", query.getQueryObject().getString("name"));
    }

    @Test
    @DisplayName("Given null segment When compile Then returns empty query")
    @Story("Compile null segment returns empty query")
    void givenNullSegment_whenCompile_thenReturnsEmptyQuery() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();
        Query query = compiler.compile((QuerySegment) null);

        assertNotNull(query);
        assertTrue(query.getQueryObject().isEmpty());
    }

    @Test
    @DisplayName("Given root with OR child When compile Then query not empty")
    @Story("Compile root with OR child")
    void givenRootWithOrChild_whenCompile_thenQueryNotEmpty() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();

        QuerySegment root = QuerySegment.root();
        root.addCondition(Condition.builder()
            .field("status")
            .operator(Operator.EQ)
            .value("active")
            .build());

        QuerySegment orSegment = QuerySegment.or();
        orSegment.addCondition(Condition.builder()
            .field("age")
            .operator(Operator.LT)
            .value(18)
            .build());
        orSegment.addCondition(Condition.builder()
            .field("vip")
            .operator(Operator.EQ)
            .value(true)
            .build());

        root.addChild(orSegment);

        Query query = compiler.compile(root);

        assertNotNull(query);
        assertFalse(query.getQueryObject().isEmpty());
    }

    /**
     * IN condition must compile to $in: [a, b] (flat array), not $in: [[a, b]].
     * Root with single condition yields query without $and wrapper.
     */
    @Test
    @DisplayName("Given IN condition When compile Then $in is flat array not nested")
    @Story("Compile IN condition as flat array")
    void givenInCondition_whenCompile_thenInIsFlatArrayNotNested() {
        LegacyQuerySegmentCompiler compiler = new MongoQueryCompiler();

        QuerySegment root = QuerySegment.root();
        root.addCondition(Condition.builder()
            .field("name")
            .operator(Operator.IN)
            .value(Arrays.asList("Q0", "Q2"))
            .build());

        Query query = compiler.compile(root);
        assertNotNull(query);

        Document queryObject = query.getQueryObject();
        Document nameOp = (Document) queryObject.get("name");
        assertNotNull(nameOp);
        @SuppressWarnings("unchecked")
        List<Object> inValues = (List<Object>) nameOp.get(IN);
        assertNotNull(inValues);
        assertEquals(2, inValues.size(), "$in must be [Q0, Q2], not [[Q0, Q2]]");
        assertTrue(inValues.contains("Q0"));
        assertTrue(inValues.contains("Q2"));
        assertFalse(inValues.get(0) instanceof List);
    }
}

