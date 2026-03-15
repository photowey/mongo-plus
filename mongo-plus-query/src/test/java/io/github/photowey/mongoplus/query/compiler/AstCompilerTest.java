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
package io.github.photowey.mongoplus.query.compiler;

import java.util.List;
import java.util.regex.Pattern;

import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.core.constant.MongoPlusConstants;
import io.github.photowey.mongoplus.core.geo.Box;
import io.github.photowey.mongoplus.core.geo.Circle;
import io.github.photowey.mongoplus.core.geo.Point;
import io.github.photowey.mongoplus.core.geo.Polygon;
import io.github.photowey.mongoplus.dsl.ast.enums.Operator;
import io.github.photowey.mongoplus.dsl.ast.node.AstNode;
import io.github.photowey.mongoplus.dsl.ast.node.ConditionNode;
import io.github.photowey.mongoplus.dsl.ast.node.LogicalNode;
import io.github.photowey.mongoplus.dsl.ast.node.RootNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoIntersectsNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.GeoWithinNode;
import io.github.photowey.mongoplus.dsl.ast.node.geo.NearNode;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AstCompilerTest - Unit tests for AST to Spring Data criteria compilation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AstCompilerTest")
class AstCompilerTest {

    @Test
    @DisplayName(
        "Given null and empty root inputs, when compiling, then returns empty query null criteria "
            + "and empty bson"
    )
    @Story("Null or empty root returns empty query and criteria")
    void givenNullAndEmptyRootInputs_whenCompiling_thenReturnsEmptyQueryNullCriteriaAndEmptyBson() {
        // Given
        AstCompiler compiler = new AstCompiler();
        RootNode root = new RootNode();

        // When
        Query query = compiler.compile(root);
        Criteria criteria = compiler.compile((AstNode) null);
        Document bson = compiler.compileToBson(null);

        // Then
        assertTrue(query.getQueryObject().isEmpty());
        assertNull(criteria);
        assertTrue(bson.isEmpty());
    }

    @Test
    @DisplayName(
        "Given condition operators, when compiling, then maps them to the expected MongoDB "
            + "criteria objects"
    )
    @Story("Condition operators map to expected MongoDB criteria")
    void givenConditionOperators_whenCompiling_thenMapsThemToTheExpectedMongoDbCriteriaObjects() {
        // Given
        AstCompiler compiler = new AstCompiler();

        // When
        Document eq = compiler.compile(ConditionNode.builder().field("name").operator(Operator.EQ).value("Tom").build())
            .getCriteriaObject();
        Document in = compiler.compile(ConditionNode.builder()
                .field("status")
                .operator(Operator.IN)
                .value(List.of("OPEN", "READY"))
                .build())
            .getCriteriaObject();
        Document like = compiler.compile(ConditionNode.like("name", "Tom")).getCriteriaObject();
        Document between = compiler.compile(ConditionNode.builder()
                .field("age")
                .operator(Operator.BETWEEN)
                .value(18)
                .secondValue(30)
                .build())
            .getCriteriaObject();
        Document exists = compiler.compile(ConditionNode.builder()
                .field("profile")
                .operator(Operator.EXISTS)
                .value(true)
                .build())
            .getCriteriaObject();
        Document fallback = compiler.compile(ConditionNode.builder()
                .field("status")
                .operator(Operator.SET)
                .value("ACTIVE")
                .build())
            .getCriteriaObject();

        // Then
        assertEquals("Tom", eq.get("name"));
        assertEquals(List.of("OPEN", "READY"), ((Document) in.get("status")).get(MongoPlusConstants.IN));
        assertTrue(((Pattern) like.get("name")).pattern().contains("\\QTom\\E"));
        assertEquals(18, ((Document) between.get("age")).get(MongoPlusConstants.GTE));
        assertEquals(30, ((Document) between.get("age")).get(MongoPlusConstants.LTE));
        assertEquals(true, ((Document) exists.get("profile")).get(MongoPlusConstants.EXISTS));
        assertEquals("ACTIVE", fallback.get("status"));
    }

    @Test
    @DisplayName(
        "Given logical nodes and root wrappers, when compiling, then builds matching and or not "
            + "queries and bson"
    )
    @Story("Logical nodes build AND/OR/NOT queries and BSON")
    void givenLogicalNodesAndRootWrappers_whenCompiling_thenBuildsMatchingAndOrNotQueriesAndBson() {
        // Given
        AstCompiler compiler = new AstCompiler();
        LogicalNode andNode = LogicalNode.and()
            .addCondition(ConditionNode.eq("status", "ACTIVE"))
            .addCondition(ConditionNode.gt("age", 18));
        LogicalNode orNode = LogicalNode.or()
            .addCondition(ConditionNode.eq("city", "Shanghai"))
            .addCondition(ConditionNode.eq("city", "Hangzhou"));
        LogicalNode notNode = LogicalNode.not().addCondition(ConditionNode.eq("deleted", true));
        RootNode root = new RootNode(andNode);

        // When
        Query rootQuery = compiler.compile(root);
        Criteria orCriteria = compiler.compile(orNode);
        Criteria notCriteria = compiler.compile(notNode);
        Document rootBson = compiler.compileToBson(root);

        // Then
        assertTrue(rootQuery.getQueryObject().containsKey(MongoPlusConstants.AND));
        assertTrue(orCriteria.getCriteriaObject().containsKey(MongoPlusConstants.OR));
        assertTrue(notCriteria.getCriteriaObject().containsKey("$nor"));
        assertTrue(rootBson.containsKey(MongoPlusConstants.AND));
    }

    @Test
    @DisplayName(
        "Given geo ast nodes, when compiling, then produces near geoWithin and geoIntersects "
            + "criteria"
    )
    @Story("Geo AST nodes produce near, geoWithin, geoIntersects criteria")
    void givenGeoAstNodes_whenCompiling_thenProducesNearGeoWithinAndGeoIntersectsCriteria() {
        // Given
        AstCompiler compiler = new AstCompiler();
        NearNode nearNode = NearNode.builder()
            .field("location")
            .longitude(120.0)
            .latitude(30.0)
            .maxDistance(1000.0)
            .build();
        GeoWithinNode circleNode = GeoWithinNode.builder()
            .field("location")
            .shape(Circle.of(120.0, 30.0, 1000.0))
            .build();
        GeoWithinNode boxNode = GeoWithinNode.builder()
            .field("location")
            .shape(Box.of(119.0, 29.0, 121.0, 31.0))
            .build();
        GeoWithinNode polygonNode = GeoWithinNode.builder()
            .field("location")
            .shape(Polygon.of(
                Point.of(120.0, 30.0),
                Point.of(121.0, 30.0),
                Point.of(121.0, 31.0),
                Point.of(120.0, 30.0)
            ))
            .build();
        GeoIntersectsNode intersectsNode = GeoIntersectsNode.builder()
            .field("location")
            .shape(Point.of(120.0, 30.0))
            .build();

        // When
        Document near = compiler.compile(nearNode).getCriteriaObject();
        Document circle = compiler.compile(circleNode).getCriteriaObject();
        Document box = compiler.compile(boxNode).getCriteriaObject();
        Document polygon = compiler.compile(polygonNode).getCriteriaObject();
        Document intersects = compiler.compile(intersectsNode).getCriteriaObject();

        // Then
        assertNotNull(((Document) near.get("location")).get(MongoPlusConstants.NEAR));
        assertNotNull(((Document) near.get("location")).get("$maxDistance"));
        assertNotNull(((Document) circle.get("location")).get(MongoPlusConstants.GEO_WITHIN));
        assertNotNull(((Document) box.get("location")).get(MongoPlusConstants.GEO_WITHIN));
        assertNotNull(((Document) polygon.get("location")).get(MongoPlusConstants.GEO_WITHIN));
        assertNotNull(((Document) intersects.get("location")).get(MongoPlusConstants.GEO_INTERSECTS));
    }
}
