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
package io.github.photowey.mongoplus.executor.engine;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.dsl.support.QueryDSL;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.executor.integration.engine.DefaultMongoEngine;
import io.github.photowey.mongoplus.executor.integration.engine.MongoEngine;
import io.github.photowey.mongoplus.executor.integration.query.EngineQuery;
import io.github.photowey.mongoplus.executor.integration.service.DefaultQueryService;
import io.github.photowey.mongoplus.executor.integration.service.QueryService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * QueryServiceTest - Tests for engine-style query service behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("QueryServiceTest")
class QueryServiceTest {

    @Test
    @DisplayName("Given metadata query When executing one Then query executor sees resolved metadata field")
    @Story("Query executor sees resolved metadata field for one")
    void givenMetadataQuery_whenExecutingOne_thenQueryExecutorSeesResolvedMetadataField() {
        // Given
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        QueryService queryService = new DefaultQueryService(queryExecutor);

        // When
        queryService.createQuery(UserDocument.class)
            .eq(UserDocumentColumns.USER_NAME, "tom")
            .eq(UserDocumentColumns.ADDRESS_CITY, "Shanghai")
            .one();

        // Then
        assertSame(UserDocument.class, queryExecutor.lastEntityClass);
        assertNotNull(queryExecutor.lastWrapper);
        assertEquals(2, queryExecutor.lastWrapper.getConditions().size());
        assertEquals("user_name", queryExecutor.lastWrapper.getConditions().get(0).getField());
        assertEquals("address.city", queryExecutor.lastWrapper.getConditions().get(1).getField());
    }

    @Test
    @DisplayName("Given lambda query When executing list Then query executor sees resolved lambda field")
    @Story("Query executor sees resolved lambda field for list")
    void givenLambdaQuery_whenExecutingList_thenQueryExecutorSeesResolvedLambdaField() {
        // Given
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        QueryService queryService = new DefaultQueryService(queryExecutor);

        // When
        queryService.createLambdaQuery(UserDocument.class)
            .eq(UserDocument::getUserName, "tom")
            .eq(UserDocumentColumns.ADDRESS_CITY, "Shanghai")
            .list();

        // Then
        assertSame(UserDocument.class, queryExecutor.lastEntityClass);
        assertEquals(2, queryExecutor.lastWrapper.getConditions().size());
        assertEquals("userName", queryExecutor.lastWrapper.getConditions().get(0).getField());
        assertEquals("address.city", queryExecutor.lastWrapper.getConditions().get(1).getField());
    }

    @Test
    @DisplayName("Given paged query shortcuts When executing page "
        + "Then query executor receives default and explicit pagination")
    @Story("Query executor receives default and explicit pagination")
    void givenPagedQueryShortcuts_whenExecutingPage_thenQueryExecutorReceivesDefaultAndExplicitPagination() {
        // Given
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        QueryService queryService = new DefaultQueryService(queryExecutor);

        // When
        queryService.createQuery(UserDocument.class).page();
        Page<UserDocument> firstPage = queryExecutor.lastPage;
        queryService.createQuery(UserDocument.class).page(2);
        Page<UserDocument> secondPage = queryExecutor.lastPage;
        queryService.createQuery(UserDocument.class).page(3, 20);
        Page<UserDocument> thirdPage = queryExecutor.lastPage;

        // Then
        assertEquals(1L, firstPage.getCurrent());
        assertEquals(10L, firstPage.getSize());
        assertEquals(2L, secondPage.getCurrent());
        assertEquals(10L, secondPage.getSize());
        assertEquals(3L, thirdPage.getCurrent());
        assertEquals(20L, thirdPage.getSize());
    }

    @Test
    @DisplayName("Given engine query as QueryDSL When shaping query Then executor sees projection sort and pagination")
    @Story("EngineQuery inherits query shaping through QueryDSL")
    void givenEngineQueryAsQueryDsl_whenShapingQuery_thenExecutorSeesProjectionSortAndPagination() {
        // Given
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        QueryService queryService = new DefaultQueryService(queryExecutor);
        QueryDSL<UserDocument, EngineQuery<UserDocument>> queryDsl = queryService.createQuery(UserDocument.class);

        // When
        queryDsl.select(UserDocumentColumns.USER_NAME)
            .orderByDesc(UserDocumentColumns.USER_NAME)
            .paginate(2, 5)
            .list();

        // Then
        assertEquals(1, queryExecutor.lastWrapper.getProjections().size());
        assertEquals("user_name", queryExecutor.lastWrapper.getProjections().get(0));
        assertEquals(1, queryExecutor.lastWrapper.getSorts().size());
        assertEquals("user_name", queryExecutor.lastWrapper.getSorts().get(0).getField());
        assertEquals(5L, queryExecutor.lastWrapper.getSkip());
        assertEquals(5L, queryExecutor.lastWrapper.getLimit());
    }

    @Test
    @DisplayName("Given engine When accessing queryService Then returns configured query service")
    @Story("Engine returns configured query service")
    void givenEngine_whenAccessingQueryService_thenReturnsConfiguredQueryService() {
        // Given
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        QueryService queryService = new DefaultQueryService(queryExecutor);
        MongoEngine mongoEngine = new DefaultMongoEngine(queryService);

        // When
        QueryService actual = mongoEngine.queryService();

        // Then
        assertSame(queryService, actual);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserDocument implements Serializable {

        private static final long serialVersionUID = 1L;

        private String userName;
        private Address address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Address implements Serializable {

        private static final long serialVersionUID = 1L;

        private String city;
    }

    static final class UserDocumentColumns implements Serializable {

        static final FieldMetadata<String> USER_NAME = FieldMetadata.<String>builder()
            .name("userName")
            .column("user_name")
            .path("user_name")
            .type(String.class)
            .entityType(UserDocument.class)
            .build();

        static final FieldMetadata<String> ADDRESS_CITY = FieldMetadata.<String>builder()
            .name("city")
            .column("city")
            .path("address.city")
            .type(String.class)
            .entityType(UserDocument.class)
            .build();
        private static final long serialVersionUID = -1412282520709692116L;

        private UserDocumentColumns() {
        }
    }

    @SuppressWarnings("all")
    private static final class RecordingQueryExecutor implements QueryExecutor {

        private AbstractWrapper<UserDocument> lastWrapper;
        private Class<UserDocument> lastEntityClass;
        private Page<UserDocument> lastPage;

        @Override
        public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;

            return Collections.emptyList();
        }

        @Override
        public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;

            return null;
        }

        @Override
        public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
            return 0L;
        }

        @Override
        public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;

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
            this.lastPage = (Page<UserDocument>) page;
            this.lastWrapper = (AbstractWrapper<UserDocument>) wrapper;
            this.lastEntityClass = (Class<UserDocument>) entityClass;

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
