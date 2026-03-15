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
package io.github.photowey.mongoplus.mapper;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * MongoMapperDefaultMethodTest - Unit tests for MongoMapper default alias methods.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("DefaultMethodMongoMapperTest")
class DefaultMethodMongoMapperTest {

    @Test
    @DisplayName(
        "Given mapper default aliases, when invoking list one and count, then delegates to the "
            + "underlying select methods"
    )
    @Story("Mapper default aliases delegate to underlying select methods")
    void givenMapperDefaultAliases_whenInvokingListOneAndCount_thenDelegatesToTheUnderlyingSelectMethods() {
        // Given
        RecordingMongoMapper mapper = new RecordingMongoMapper();
        QueryWrapper<String> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<String> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        // When
        List<String> queryList = mapper.list(queryWrapper);
        List<String> lambdaList = mapper.list(lambdaQueryWrapper);
        String queryOne = mapper.one(queryWrapper);
        String lambdaOne = mapper.one(lambdaQueryWrapper);
        long count = mapper.count(queryWrapper);

        // Then
        assertSame(mapper.queryListResult, queryList);
        assertSame(mapper.lambdaListResult, lambdaList);
        assertEquals("query-one", queryOne);
        assertEquals("lambda-one", lambdaOne);
        assertEquals(9L, count);
        assertSame(queryWrapper, mapper.lastQueryWrapper);
        assertSame(lambdaQueryWrapper, mapper.lastLambdaQueryWrapper);
        assertSame(queryWrapper, mapper.lastCountWrapper);
    }

    private static final class RecordingMongoMapper implements MongoMapper<String> {

        private final List<String> queryListResult = List.of("Q");
        private final List<String> lambdaListResult = List.of("L");
        private QueryWrapper<String> lastQueryWrapper;
        private LambdaQueryWrapper<String> lastLambdaQueryWrapper;
        private AbstractWrapper<String> lastCountWrapper;

        @Override
        public String selectById(Serializable id) {
            return null;
        }

        @Override
        public List<String> selectList(QueryWrapper<String> wrapper) {
            this.lastQueryWrapper = wrapper;
            return this.queryListResult;
        }

        @Override
        public List<String> selectList(LambdaQueryWrapper<String> wrapper) {
            this.lastLambdaQueryWrapper = wrapper;
            return this.lambdaListResult;
        }

        @Override
        public String selectOne(QueryWrapper<String> wrapper) {
            this.lastQueryWrapper = wrapper;
            return "query-one";
        }

        @Override
        public String selectOne(LambdaQueryWrapper<String> wrapper) {
            this.lastLambdaQueryWrapper = wrapper;
            return "lambda-one";
        }

        @Override
        public long selectCount(AbstractWrapper<String> wrapper) {
            this.lastCountWrapper = wrapper;
            return 9L;
        }

        @Override
        public boolean exists(AbstractWrapper<String> wrapper) {
            return false;
        }

        @Override
        public Page<String> selectPage(Page<String> page, QueryWrapper<String> wrapper) {
            return page;
        }

        @Override
        public Page<String> selectPage(Page<String> page, LambdaQueryWrapper<String> wrapper) {
            return page;
        }

        @Override
        public boolean insert(String entity) {
            return false;
        }

        @Override
        public boolean updateById(String entity) {
            return false;
        }

        @Override
        public boolean update(String entity, AbstractWrapper<String> wrapper) {
            return false;
        }

        @Override
        public boolean deleteById(Serializable id) {
            return false;
        }

        @Override
        public boolean delete(AbstractWrapper<String> wrapper) {
            return false;
        }

        @Override
        public BatchResult insertBatch(List<String> entities) {
            return null;
        }

        @Override
        public BatchResult updateBatch(List<String> entities) {
            return null;
        }

        @Override
        public BatchResult deleteBatch(List<? extends Serializable> ids) {
            return null;
        }

        @Override
        public <O> List<O> aggregate(AggregationWrapper<String> wrapper, Class<O> outputClass) {
            return List.of();
        }
    }
}
