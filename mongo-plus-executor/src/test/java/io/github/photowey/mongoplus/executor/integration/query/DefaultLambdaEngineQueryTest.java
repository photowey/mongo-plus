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
package io.github.photowey.mongoplus.executor.integration.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DefaultLambdaEngineQueryTest - Unit tests for lambda engine query chaining behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("DefaultLambdaEngineQueryTest")
class DefaultLambdaEngineQueryTest {

    @Test
    @DisplayName(
        "Given a lambda engine query chain, when listing results, then the wrapper captures the "
            + "resolved lambda metadata"
    )
    @Story("Lambda engine query captures resolved lambda metadata in wrapper")
    void givenALambdaEngineQueryChain_whenListingResults_thenTheWrapperCapturesTheResolvedLambdaMetadata() {
        // Given
        RecordingQueryExecutor executor = new RecordingQueryExecutor();
        DefaultLambdaEngineQuery<UserDocument> query = new DefaultLambdaEngineQuery<>(executor, UserDocument.class);

        // When
        List<UserDocument> result = query
            .eq(UserDocument::getStatus, "ACTIVE")
            .ne(UserDocument::getType, "LEGACY")
            .gt(UserDocument::getAge, 18)
            .gte(UserDocument::getScore, 80)
            .lt(UserDocument::getLevel, 5)
            .lte(UserDocument::getRank, 8)
            .between(UserDocument::getCreatedAt, 1L, 2L)
            .in(UserDocument::getStatus, List.of("ACTIVE", "READY"))
            .in(UserDocument::getType, "NEW", "HOT")
            .notIn(UserDocument::getDeleted, List.of(true))
            .nin(UserDocument::getArchived, List.of(true))
            .like(UserDocument::getName, "mongo")
            .likeLeft(UserDocument::getPrefix, "pre")
            .likeRight(UserDocument::getSuffix, "suf")
            .regex(UserDocument::getMobile, "^1")
            .isNull(UserDocument::getOptionalField)
            .isNotNull(UserDocument::getRequiredField)
            .exists(UserDocument::getEmbedded, true)
            .all(UserDocument::getTags, List.of("A", "B"))
            .size(UserDocument::getTags, 2)
            .orderByAsc(UserDocument::getStatus)
            .orderByDesc(UserDocument::getCreatedAt)
            .orderBy(UserDocument::getScore, true)
            .select(UserDocument::getStatus, UserDocument::getMobile)
            .exclude(UserDocument::getDeleted)
            .limit(10)
            .skip(3)
            .and(nested -> nested.eq("city", "Shanghai"))
            .or(nested -> nested.eq("city", "Hangzhou"))
            .list();
        AbstractWrapper<UserDocument> wrapper = executor.lastWrapper;

        // Then
        assertSame(executor.selectListResult, result);
        assertSame(UserDocument.class, executor.lastEntityClass);
        assertSame(UserDocument.class, wrapper.getEntityClass());
        assertEquals(22, wrapper.getConditions().size());
        assertEquals(3, wrapper.getSorts().size());
        assertEquals(10L, wrapper.getLimit());
        assertEquals(3L, wrapper.getSkip());
        assertEquals("status", wrapper.getConditions().get(0).getField());
        assertEquals("type", wrapper.getConditions().get(1).getField());
        assertEquals("tags", wrapper.getConditions().get(19).getField());
        assertTrue(wrapper.getProjections().contains("status"));
        assertTrue(wrapper.getProjections().contains("mobile"));
        assertTrue(wrapper.getProjections().contains("-deleted"));
        assertEquals("status", wrapper.getSorts().get(0).getField());
        assertEquals("createdAt", wrapper.getSorts().get(1).getField());
        assertEquals("score", wrapper.getSorts().get(2).getField());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class UserDocument implements Serializable {

        private static final long serialVersionUID = 1L;

        private String status;
        private String type;
        private Integer age;
        private Integer score;
        private Integer level;
        private Integer rank;
        private Long createdAt;
        private Boolean deleted;
        private Boolean archived;
        private String name;
        private String prefix;
        private String suffix;
        private String mobile;
        private String optionalField;
        private String requiredField;
        private String embedded;
        private Collection<String> tags;
    }

    private static final class RecordingQueryExecutor implements QueryExecutor {

        private final List<UserDocument> selectListResult = List.of(new UserDocument());
        private AbstractWrapper<UserDocument> lastWrapper;
        private Class<UserDocument> lastEntityClass;

        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;
            return (List<T>) this.selectListResult;
        }

        @Override
        public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return null;
        }

        @Override
        public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
            return 0;
        }

        @Override
        public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return false;
        }

        @Override
        public <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return false;
        }

        @Override
        public <T> boolean update(UpdateWrapper<T> updateWrapper, Class<T> entityClass) {
            return false;
        }

        @Override
        public <T> Page<T> selectPage(Page<T> page, AbstractWrapper<T> wrapper, Class<T> entityClass) {
            return page;
        }

        @Override
        public <T> T selectById(Serializable id, Class<T> entityClass) {
            return null;
        }

        @Override
        public <T> boolean insert(T entity, String collectionName) {
            return false;
        }

        @Override
        public <T> boolean updateById(T entity, String collectionName) {
            return false;
        }

        @Override
        public boolean deleteById(Serializable id, String collectionName) {
            return false;
        }

        @Override
        public <T> BatchResult insertBatch(List<T> entities, String collectionName) {
            return null;
        }

        @Override
        public <T> BatchResult insertBatch(List<T> entities, Class<T> entityClass) {
            return null;
        }

        @Override
        public <T> BatchResult updateBatch(List<T> entities, String collectionName) {
            return null;
        }

        @Override
        public <T> BatchResult updateBatch(List<T> entities, Class<T> entityClass) {
            return null;
        }

        @Override
        public BatchResult deleteBatch(List<? extends Serializable> ids, String collectionName) {
            return null;
        }

        @Override
        public <T> BatchResult deleteBatch(List<? extends Serializable> ids, Class<T> entityClass) {
            return null;
        }
    }
}
