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
package io.github.photowey.mongoplus.aggregation;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.aggregation.stage.Stage;
import io.github.photowey.mongoplus.aggregation.stage.enums.StageType;
import io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AggregationWrapperTest - Tests for AggregationWrapper DSL.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("AggregationWrapperTest")
class AggregationWrapperTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class User implements Serializable {

        private static final long serialVersionUID = -8511722932906317767L;

        private String name;
        private Integer age;
        private String dept;
        private Double salary;
    }

    @Test
    @DisplayName("Given match with eq When build Then pipeline has one MATCH stage")
    @Story("Match with eq builds pipeline with one match stage")
    void givenMatchWithEq_whenBuild_thenPipelineHasOneMatchStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .match(w -> w.eq(User::getDept, "Engineering"));

        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
        assertEquals(StageType.MATCH, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given group sum count When build Then pipeline has one GROUP stage")
    @Story("Group with sum and count builds one group stage")
    void givenGroupSumCount_whenBuild_thenPipelineHasOneGroupStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .group(User::getDept)
            .sum(User::getSalary)
            .count();

        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
        assertEquals(StageType.GROUP, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given project with fields When build Then pipeline has one PROJECT stage")
    @Story("Project with fields builds one project stage")
    void givenProjectWithFields_whenBuild_thenPipelineHasOneProjectStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .project("name", "age");

        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
        assertEquals(StageType.PROJECT, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given sortAsc sortDesc When build Then pipeline has two SORT stages")
    @Story("Sort asc and desc build two sort stages")
    void givenSortAscSortDesc_whenBuild_thenPipelineHasTwoSortStages() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .sortAsc(User::getAge)
            .sortDesc(User::getName);

        Pipeline pipeline = wrapper.build();
        assertEquals(2, pipeline.getStages().size());
        assertEquals(StageType.SORT, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given skip and limit When build Then pipeline has SKIP and LIMIT stages")
    @Story("Skip and limit build corresponding stages")
    void givenSkipAndLimit_whenBuild_thenPipelineHasSkipAndLimitStages() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .skip(10)
            .limit(5);

        Pipeline pipeline = wrapper.build();
        assertEquals(2, pipeline.getStages().size());
        assertEquals(StageType.SKIP, pipeline.getStages().get(0).getType());
        assertEquals(StageType.LIMIT, pipeline.getStages().get(1).getType());
    }

    @Test
    @DisplayName("Given lookup When build Then pipeline has one LOOKUP stage")
    @Story("Lookup builds one lookup stage")
    void givenLookup_whenBuild_thenPipelineHasOneLookupStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .lookup("departments", "deptId", ID, "department");

        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
        assertEquals(StageType.LOOKUP, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given match group sort limit When build Then pipeline has four stage types")
    @Story("Match group sort limit build four stage types")
    void givenMatchGroupSortLimit_whenBuild_thenPipelineHasFourStageTypes() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .match(w -> w.eq(User::getDept, "Engineering"))
            .group(User::getDept)
            .sum(User::getSalary)
            .avg(User::getAge)
            .count()
            .sortDesc("salary")
            .limit(10);

        Pipeline pipeline = wrapper.build();
        List<Stage> stages = pipeline.getStages();

        assertEquals(4, stages.size());
        assertEquals(StageType.MATCH, stages.get(0).getType());
        assertEquals(StageType.GROUP, stages.get(1).getType());
        assertEquals(StageType.SORT, stages.get(2).getType());
        assertEquals(StageType.LIMIT, stages.get(3).getType());
    }

    @Test
    @DisplayName("Given group with sum avg max min count When build Then one group stage")
    @Story("Group with sum avg max min count builds one stage")
    void givenGroupWithSumAvgMaxMinCount_whenBuild_thenOneGroupStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class);

        // Test all aggregation functions
        wrapper.group("dept")
            .sum("salary", "totalSalary")
            .avg("age", "avgAge")
            .max("salary", "maxSalary")
            .min("salary", "minSalary")
            .count("employeeCount");

        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
    }

    @Test
    @DisplayName("Given class When aggregation Then returns wrapper with same entity class")
    @Story("Aggregation returns wrapper with same entity class")
    void givenClass_whenAggregation_thenReturnsWrapperWithSameEntityClass() {
        AggregationWrapper<User> wrapper = AggregationWrapper.aggregation(User.class);
        assertNotNull(wrapper);
        assertSame(User.class, wrapper.entityClass());
    }

    @Test
    @DisplayName("Given string field When group Then pipeline has GROUP stage")
    @Story("Group by string field adds group stage")
    void givenStringField_whenGroup_thenPipelineHasGroupStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class).group("dept");
        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
        assertEquals(StageType.GROUP, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given group and sum with field When build Then one stage")
    @Story("Group with sum by field produces one stage")
    void givenGroupAndSumWithField_whenBuild_thenOneStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .group("dept")
            .sum("salary");
        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
    }

    @Test
    @DisplayName("Given group and avg with alias When build Then one stage")
    @Story("Group with avg and alias produces one stage")
    void givenGroupAndAvgWithAlias_whenBuild_thenOneStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .group("dept")
            .avg("salary", "avgSalary");
        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
    }

    @Test
    @DisplayName("Given group with max min lambda When build Then one stage")
    @Story("Group with max min lambda produces one stage")
    void givenGroupWithMaxMinLambda_whenBuild_thenOneStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .group(User::getDept)
            .max(User::getSalary)
            .min(User::getSalary);
        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
    }

    @Test
    @DisplayName("Given group and count When build Then one stage")
    @Story("Group with count produces one stage")
    void givenGroupAndCount_whenBuild_thenOneStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .group("dept")
            .count();
        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
    }

    @Test
    @DisplayName("Given sum and as When build Then does not throw")
    @Story("Sum with as alias does not throw")
    void givenSumAndAs_whenBuild_thenDoesNotThrow() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .group("dept")
            .sum("salary")
            .as("total");
        assertNotNull(wrapper.build());
    }

    @Test
    @DisplayName("Given project with lambda getters When build Then PROJECT stage")
    @Story("Project with lambda getters builds project stage")
    void givenProjectWithLambdaGetters_whenBuild_thenProjectStage() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .project(User::getName, User::getAge, User::getDept);
        Pipeline pipeline = wrapper.build();
        assertEquals(1, pipeline.getStages().size());
        assertEquals(StageType.PROJECT, pipeline.getStages().get(0).getType());
    }

    @Test
    @DisplayName("Given sortAsc sortDesc by string When build Then two SORT stages")
    @Story("Sort asc and desc by string builds two sort stages")
    void givenSortAscSortDescByString_whenBuild_thenTwoSortStages() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .sortAsc("age")
            .sortDesc("salary");
        Pipeline pipeline = wrapper.build();
        assertEquals(2, pipeline.getStages().size());
        assertEquals(StageType.SORT, pipeline.getStages().get(0).getType());
        assertEquals(StageType.SORT, pipeline.getStages().get(1).getType());
    }

    @Test
    @DisplayName("Given limit then skip When build Then LIMIT then SKIP stages")
    @Story("Limit then skip builds limit then skip stages")
    void givenLimitThenSkip_whenBuild_thenLimitThenSkipStages() {
        AggregationWrapper<User> wrapper = aggregation(User.class)
            .limit(5)
            .skip(10);
        List<Stage> stages = wrapper.build().getStages();
        assertEquals(2, stages.size());
        assertEquals(StageType.LIMIT, stages.get(0).getType());
        assertEquals(StageType.SKIP, stages.get(1).getType());
    }

    @Test
    @DisplayName("Given wrapper When stages Then returns unmodifiable list")
    @Story("Wrapper stages returns unmodifiable list")
    void givenWrapper_whenStages_thenReturnsUnmodifiableList() {
        AggregationWrapper<User> wrapper = aggregation(User.class).match(w -> w.eq(User::getDept, "x"));
        List<Stage> stages = wrapper.stages();
        assertNotNull(stages);
        assertEquals(1, stages.size());
        assertThrows(UnsupportedOperationException.class, () -> stages.add(null));
    }

    @Test
    @DisplayName("Given aggregation wrapper When entityClass Then returns same class")
    @Story("Aggregation wrapper entity class returns same class")
    void givenAggregationWrapper_whenEntityClass_thenReturnsSameClass() {
        AggregationWrapper<User> wrapper = aggregation(User.class);
        assertSame(User.class, wrapper.entityClass());
    }

    @Test
    @DisplayName("Given wrapper When build twice Then both pipelines have stages")
    @Story("Wrapper build twice yields both pipelines with stages")
    void givenWrapper_whenBuildTwice_thenBothPipelinesHaveStages() {
        AggregationWrapper<User> wrapper = aggregation(User.class).limit(1);
        Pipeline p1 = wrapper.build();
        Pipeline p2 = wrapper.build();
        assertNotNull(p1);
        assertNotNull(p2);
        assertTrue(p1.getStages().size() == 1 && p2.getStages().size() == 1);
    }

    @Test
    @DisplayName("Given no group When sum then build Then throws IllegalStateException")
    @Story("Sum without group throws IllegalStateException")
    void givenNoGroup_whenSumThenBuild_thenThrowsIllegalStateException() {
        AggregationWrapper<User> wrapper = aggregation(User.class);
        assertThrows(IllegalStateException.class, () -> wrapper.sum("salary").build());
    }

    private static <T> AggregationWrapper<T> aggregation(Class<T> clazz) {
        return AggregationWrapper.aggregation(clazz);
    }
}

