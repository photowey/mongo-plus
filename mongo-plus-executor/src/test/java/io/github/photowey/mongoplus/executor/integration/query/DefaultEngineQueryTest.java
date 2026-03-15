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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.metadata.EntityMetadata;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DefaultEngineQueryTest - Unit tests for default engine query chaining and terminal operations.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("DefaultEngineQueryTest")
class DefaultEngineQueryTest {

    @AfterEach
    void resetEntityResolver() {
        EntityResolver.resetEntityMetadataResolver();
        EntityResolver.clearCache();
    }

    @Test
    @DisplayName(
        "Given a rich engine query chain, when listing results, then the wrapper captures the "
            + "configured conditions projections and sorting"
    )
    @Story("User columns")
    void givenRichEngineQueryChain_whenListingResults_thenWrapperCapturesConfiguredState(
    ) {
        // Given
        RecordingQueryExecutor executor = new RecordingQueryExecutor();
        DefaultEngineQuery<UserDocument> query = new DefaultEngineQuery<>(executor, UserDocument.class);

        // When
        List<UserDocument> result = query
            .eq("status", "ACTIVE")
            .eq(UserColumns.STATUS, "READY")
            .ne("type", "LEGACY")
            .ne(UserColumns.TYPE, "OLD")
            .gt("age", 18)
            .gt(UserColumns.AGE, 20)
            .gte("score", 60)
            .gte(UserColumns.SCORE, 80)
            .lt("level", 5)
            .lt(UserColumns.LEVEL, 4)
            .lte("rank", 10)
            .lte(UserColumns.RANK, 8)
            .between("createdAt", 1, 2)
            .between(UserColumns.CREATED_AT, 3, 4)
            .in("status", List.of("ACTIVE", "READY"))
            .in("code", "A", "B")
            .in(UserColumns.TYPE, List.of("NEW", "HOT"))
            .in(UserColumns.STATUS, "OPEN", "CLOSE")
            .notIn("deleted", List.of(true))
            .notIn(UserColumns.DELETED, List.of(false))
            .nin("archived", List.of(false))
            .nin(UserColumns.ARCHIVED, List.of(true))
            .like("name", "photo")
            .like(UserColumns.NAME, "mongo")
            .likeLeft("prefix", "pre")
            .likeLeft(UserColumns.PREFIX, "left")
            .likeRight("suffix", "suf")
            .likeRight(UserColumns.SUFFIX, "right")
            .regex("mobile", "^1")
            .regex(UserColumns.MOBILE, "^2")
            .isNull("optionalField")
            .isNull(UserColumns.OPTIONAL_FIELD)
            .isNotNull("requiredField")
            .isNotNull(UserColumns.REQUIRED_FIELD)
            .exists("embedded", true)
            .exists(UserColumns.EMBEDDED, false)
            .all("tags", List.of("a", "b"))
            .all(UserColumns.TAGS, List.of("c", "d"))
            .size("tags", 2)
            .size(UserColumns.TAGS, 4)
            .select("status", "name")
            .select(UserColumns.TYPE, UserColumns.MOBILE)
            .exclude("deleted")
            .exclude(UserColumns.ARCHIVED)
            .orderByAsc("status")
            .orderByAsc(UserColumns.TYPE)
            .orderByDesc("createdAt")
            .orderByDesc(UserColumns.CREATED_AT)
            .orderBy(true, "score")
            .orderBy(false, UserColumns.LEVEL)
            .limit(20)
            .skip(5)
            .and(nested -> nested.eq("city", "Shanghai"))
            .or(nested -> nested.eq("city", "Hangzhou"))
            .list();
        AbstractWrapper<UserDocument> wrapper = executor.lastWrapper;

        // Then
        assertSame(executor.selectListResult, result);
        assertEquals(UserDocument.class, executor.lastEntityClass);
        assertNotNull(wrapper);
        assertEquals(42, wrapper.getConditions().size());
        assertEquals(6, wrapper.getSorts().size());
        assertTrue(wrapper.getProjections().contains("status"));
        assertTrue(wrapper.getProjections().contains("type"));
        assertTrue(wrapper.getProjections().contains("mobile"));
        assertTrue(wrapper.getProjections().contains("-deleted"));
        assertTrue(wrapper.getProjections().contains("-archived"));
        assertEquals(20L, wrapper.getLimit());
        assertEquals(5L, wrapper.getSkip());
        Condition andCondition = wrapper.getConditions().get(wrapper.getConditions().size() - 2);
        Condition orCondition = wrapper.getConditions().get(wrapper.getConditions().size() - 1);
        assertEquals(Segment.AND, andCondition.segment());
        assertEquals(Segment.OR, orCondition.segment());
        assertNotNull(andCondition.nestedWrapper());
        assertNotNull(orCondition.nestedWrapper());
    }

    @Test
    @DisplayName(
        "Given terminal engine query operations, when executing one count exists and page, then "
            + "delegates with the resolved wrapper and collection metadata"
    )
    @Story("User columns")
    void givenTerminalEngineQueryOperations_whenExecutingThem_thenDelegatesWithResolvedMetadata(
    ) {
        // Given
        RecordingQueryExecutor executor = new RecordingQueryExecutor();
        EntityResolver.setEntityMetadataResolver(entityClass -> EntityMetadata.builder()
            .entityClass(entityClass)
            .collectionName("users")
            .build());
        DefaultEngineQuery<UserDocument> query = new DefaultEngineQuery<>(executor, UserDocument.class);

        // When
        UserDocument one = query.eq("status", "ACTIVE").one();
        long count = query.count();
        boolean exists = query.exists();
        Page<UserDocument> defaultPage = query.page();
        Page<UserDocument> secondPage = query.page(2);
        Page<UserDocument> sizedPage = query.page(3, 15);

        // Then
        assertSame(executor.selectOneResult, one);
        assertEquals(7L, count);
        assertTrue(exists);
        assertEquals("users", executor.lastCollectionName);
        assertEquals(1L, defaultPage.getCurrent());
        assertEquals(10L, defaultPage.getSize());
        assertEquals(2L, secondPage.getCurrent());
        assertEquals(10L, secondPage.getSize());
        assertEquals(3L, sizedPage.getCurrent());
        assertEquals(15L, sizedPage.getSize());
        assertEquals(1, executor.selectOneCalls);
        assertEquals(1, executor.selectCountCalls);
        assertEquals(1, executor.existsCalls);
        assertEquals(3, executor.selectPageCalls);
    }

    private static final class UserDocument implements Serializable {

        private static final long serialVersionUID = 1L;
    }

    private static final class UserColumns {

        private static final FieldMetadata<String> STATUS = field("status");
        private static final FieldMetadata<String> TYPE = field("type");
        private static final FieldMetadata<Integer> AGE = field("age");
        private static final FieldMetadata<Integer> SCORE = field("score");
        private static final FieldMetadata<Integer> LEVEL = field("level");
        private static final FieldMetadata<Integer> RANK = field("rank");
        private static final FieldMetadata<Integer> CREATED_AT = field("createdAt");
        private static final FieldMetadata<Boolean> DELETED = field("deleted");
        private static final FieldMetadata<Boolean> ARCHIVED = field("archived");
        private static final FieldMetadata<String> NAME = field("name");
        private static final FieldMetadata<String> PREFIX = field("prefix");
        private static final FieldMetadata<String> SUFFIX = field("suffix");
        private static final FieldMetadata<String> MOBILE = field("mobile");
        private static final FieldMetadata<String> OPTIONAL_FIELD = field("optionalField");
        private static final FieldMetadata<String> REQUIRED_FIELD = field("requiredField");
        private static final FieldMetadata<String> EMBEDDED = field("embedded");
        private static final FieldMetadata<Collection<?>> TAGS = field("tags");

        private UserColumns() {
        }

        @SuppressWarnings("unchecked")
        private static <T> FieldMetadata<T> field(String path) {
            return (FieldMetadata<T>) FieldMetadata.builder()
                .name(path)
                .column(path)
                .path(path)
                .entityType(UserDocument.class)
                .build();
        }
    }

    private static final class RecordingQueryExecutor implements QueryExecutor {

        private final List<UserDocument> selectListResult = List.of(new UserDocument());
        private final UserDocument selectOneResult = new UserDocument();
        private AbstractWrapper<UserDocument> lastWrapper;
        private Class<UserDocument> lastEntityClass;
        private String lastCollectionName;
        private int selectOneCalls;
        private int selectCountCalls;
        private int existsCalls;
        private int selectPageCalls;

        @SuppressWarnings("unchecked")
        @Override
        public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;
            return (List<T>) this.selectListResult;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.selectOneCalls++;
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;
            return (T) this.selectOneResult;
        }

        @Override
        public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
            this.selectCountCalls++;
            this.lastCollectionName = collectionName;
            return 7L;
        }

        @Override
        public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.existsCalls++;
            return true;
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
            this.selectPageCalls++;
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
