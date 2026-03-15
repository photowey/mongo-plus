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
package io.github.photowey.mongoplus.examples.spring.boot.b2x.aggregation;

import java.io.Serializable;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.query.Query;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.AbstractMongoTest;
import io.github.photowey.mongoplus.examples.spring.boot.b2x.core.domain.document.UserDocument;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper.aggregation;
import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AggregationWrapperIntegrationTest - Integration tests for AggregationWrapper under Testcontainers MongoDB.
 * Covers match, group, project, sort, limit, skip and pipeline execution.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/08
 */
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AggregationWrapperIntegrationTest")
class AggregationWrapperIntegrationTest extends AbstractMongoTest {

    @Autowired
    private AggregationExecutor aggregationExecutor;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * DTO for $group output: { _id: "...", count: N }
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupByNameResult implements Serializable {

        private static final long serialVersionUID = -6218684182918863489L;

        @Id
        @Field(ID)
        private String id;
        private Integer count;
    }

    @BeforeEach
    void clearUsers() {
        this.mongoTemplate.remove(new Query(), UserDocument.class);
    }

    @Nested
    @DisplayName("Match")
    @Feature("MatchTests")
    class MatchTests {

        @Test
        @DisplayName("Given two users When match eq and gt Then returns one matching document")
        @Story("Match documents by equality and greater-than")
        void givenTwoUsers_whenMatchEqAndGt_thenReturnsOneMatchingDocument() {
            mongoTemplate.insert(new UserDocument("M1", "m1@e.com", 20));
            mongoTemplate.insert(new UserDocument("M2", "m2@e.com", 30));
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class)
                .match(m -> m.eq(UserDocument::getName, "M1").gt(UserDocument::getAge, 18));
            Pipeline pipeline = w.build();
            List<UserDocument> list = aggregationExecutor.execute(pipeline, UserDocument.class, UserDocument.class);
            assertEquals(1, list.size());
            assertEquals("M1", list.get(0).getName());
        }
    }

    @Nested
    @DisplayName("Group")
    @Feature("GroupTests")
    class GroupTests {

        @Test
        @DisplayName("Given same name users When group by name with count Then returns one row per name with count")
        @Story("Group documents by field with count aggregation")
        void givenSameNameUsers_whenGroupByNameWithCount_thenReturnsOneRowPerNameWithCount() {
            mongoTemplate.insert(new UserDocument("G", "g1@e.com", 10));
            mongoTemplate.insert(new UserDocument("G", "g2@e.com", 20));
            mongoTemplate.insert(new UserDocument("H", "h@e.com", 15));
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class)
                .group(UserDocument::getName)
                .count("count");
            Pipeline pipeline = w.build();
            List<GroupByNameResult> list = aggregationExecutor.execute(
                pipeline,
                UserDocument.class,
                GroupByNameResult.class
            );
            assertEquals(2, list.size());
            GroupByNameResult g = list.stream().filter(r -> "G".equals(r.getId())).findFirst().orElse(null);
            assertNotNull(g);
            assertEquals(2, g.getCount());
        }

        @Test
        @DisplayName("Given two same name users When group with sum avg count Then result has totalAge avgAge cnt")
        @Description("Integration: $group with sum, avg, count aggregations. Verifies complex group output.")
        @Story("Group with sum, avg, and count aggregations")
        @Severity(SeverityLevel.CRITICAL)
        void givenTwoSameNameUsers_whenGroupWithSumAvgCount_thenResultHasTotalAgeAvgAgeCnt() {
            mongoTemplate.insert(new UserDocument("S", "s1@e.com", 10));
            mongoTemplate.insert(new UserDocument("S", "s2@e.com", 20));
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class)
                .group("name")
                .sum("age", "totalAge")
                .avg("age", "avgAge")
                .count("cnt");
            Pipeline pipeline = w.build();
            List<Document> list = aggregationExecutor.execute(pipeline, UserDocument.class, Document.class);
            assertEquals(1, list.size());
            Document doc = list.get(0);
            assertTrue(doc.containsKey("totalAge"));
            assertTrue(doc.containsKey("avgAge"));
            assertTrue(doc.containsKey("cnt"));
        }
    }

    @Nested
    @DisplayName("Project")
    @Feature("ProjectTests")
    class ProjectTests {

        @Test
        @DisplayName("Given one user When project name and age Then result has name and age")
        @Story("Project selected fields in aggregation")
        void givenOneUser_whenProjectNameAndAge_thenResultHasNameAndAge() {
            mongoTemplate.insert(new UserDocument("P", "p@e.com", 25));
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class)
                .project(UserDocument::getName, UserDocument::getAge);
            Pipeline pipeline = w.build();
            List<UserDocument> list = aggregationExecutor.execute(pipeline, UserDocument.class, UserDocument.class);
            assertEquals(1, list.size());
            assertNotNull(list.get(0).getName());
            assertNotNull(list.get(0).getAge());
        }
    }

    @Nested
    @DisplayName("Sort Limit Skip")
    @Feature("SortLimitSkipTests")
    class SortLimitSkipTests {

        @Test
        @DisplayName("Given five users When sortAsc skip 1 limit 2 Then returns second and third by age")
        @Story("Sort, skip, and limit in aggregation pipeline")
        void givenFiveUsers_whenSortAscSkip1Limit2_thenReturnsSecondAndThirdByAge() {
            for (int i = 0; i < 5; i++) {
                mongoTemplate.insert(new UserDocument("U" + i, "u@e.com", 30 - i));
            }
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class)
                .sortAsc(UserDocument::getAge)
                .skip(1)
                .limit(2);
            Pipeline pipeline = w.build();
            List<UserDocument> list = aggregationExecutor.execute(pipeline, UserDocument.class, UserDocument.class);
            assertEquals(2, list.size());
            assertEquals(27, list.get(0).getAge());
            assertEquals(28, list.get(1).getAge());
        }

        @Test
        @DisplayName("Given two users When sortDesc by age Then first is older")
        @Story("Sort documents descending")
        void givenTwoUsers_whenSortDescByAge_thenFirstIsOlder() {
            mongoTemplate.insert(new UserDocument("A", "a@e.com", 10));
            mongoTemplate.insert(new UserDocument("B", "b@e.com", 20));
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class).sortDesc(UserDocument::getAge);
            Pipeline pipeline = w.build();
            List<UserDocument> list = aggregationExecutor.execute(pipeline, UserDocument.class, UserDocument.class);
            assertEquals(2, list.size());
            assertEquals(20, list.get(0).getAge());
            assertEquals(10, list.get(1).getAge());
        }
    }

    @Nested
    @DisplayName("Full pipeline")
    @Feature("FullPipelineTests")
    class FullPipelineTests {

        @Test
        @DisplayName("Given ten users When match gte lte project sortDesc limit 2 Then top two ages 26 and 25")
        @Description("Full pipeline integration: match + project + sortDesc + limit against Testcontainers MongoDB.")
        @Story("Full pipeline: match, project, sort, and limit")
        @Severity(SeverityLevel.CRITICAL)
        void givenTenUsers_whenMatchGteLteProjectSortDescLimit2_thenTopTwoAges26And25() {
            for (int i = 0; i < 10; i++) {
                mongoTemplate.insert(new UserDocument("F" + i, "f@e.com", 20 + i));
            }
            AggregationWrapper<UserDocument> w = aggregation(UserDocument.class)
                .match(m -> m.gte(UserDocument::getAge, 22).lte(UserDocument::getAge, 26))
                .project("name", "age")
                .sortDesc("age")
                .limit(2);
            Pipeline pipeline = w.build();
            List<UserDocument> list = aggregationExecutor.execute(pipeline, UserDocument.class, UserDocument.class);
            assertEquals(2, list.size());
            assertEquals(26, list.get(0).getAge());
            assertEquals(25, list.get(1).getAge());
        }
    }
}

