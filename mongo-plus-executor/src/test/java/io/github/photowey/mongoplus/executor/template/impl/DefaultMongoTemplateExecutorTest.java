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
package io.github.photowey.mongoplus.executor.template.impl;

import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import io.github.photowey.mongoplus.core.metadata.EntityMetadata;
import io.github.photowey.mongoplus.core.metadata.EntityMetadataResolver;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.executor.compiler.QueryCompiler;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.ID;
import static io.github.photowey.mongoplus.core.constant.MongoPlusConstants.SET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DefaultMongoTemplateExecutorTest - Tests for runtime wrapper query compilation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("DefaultMongoTemplateExecutorTest")
class DefaultMongoTemplateExecutorTest {

    @AfterEach
    void resetEntityResolver() {
        EntityResolver.resetEntityMetadataResolver();
        EntityResolver.clearCache();
    }

    @Test
    @DisplayName("Given custom QueryCompiler When building wrapper query Then executor uses injected compiler")
    @Story("Use injected query compiler for wrapper query")
    void givenCustomQueryCompiler_whenBuildingWrapperQuery_thenExecutorUsesInjectedCompiler() {
        RecordingQueryCompiler queryCompiler = new RecordingQueryCompiler();
        TestExecutor executor = new TestExecutor(queryCompiler);
        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");

        Query query = executor.exposeBuildQuery(wrapper);

        assertTrue(queryCompiler.wrapperCompileCalled);
        assertSame(wrapper, queryCompiler.lastWrapper);
        assertSame(queryCompiler.queryToReturn, query);
    }

    @Test
    @DisplayName("Given metadata resolver When building id query Then executor uses resolved id field and _id column")
    @Story("Build id query using resolved metadata")
    void givenMetadataResolver_whenBuildingIdQuery_thenExecutorUsesResolvedIdFieldAndIdColumn() {
        EntityResolver.setEntityMetadataResolver(this.metadataResolver());
        TestExecutor executor = new TestExecutor(wrapper -> new Query());
        MetadataEntity entity = new MetadataEntity(7L, "Tom", null, 18);

        Query query = executor.exposeBuildIdQuery(entity);

        assertNotNull(query);
        assertEquals(7L, query.getQueryObject().get(ID));
    }

    @Test
    @DisplayName("Given metadata resolver When building update Then executor uses mapped fields and skips id and nulls")
    @Story("Build update from entity using mapped fields")
    void givenMetadataResolver_whenBuildingUpdate_thenExecutorUsesMappedFieldsAndSkipsIdAndNulls() {
        EntityResolver.setEntityMetadataResolver(this.metadataResolver());
        TestExecutor executor = new TestExecutor(wrapper -> new Query());
        MetadataEntity entity = new MetadataEntity(7L, "Tom", null, 18);

        Update update = executor.exposeBuildUpdateFromEntity(entity);
        Document setDocument = (Document) update.getUpdateObject().get(SET);

        assertNotNull(setDocument);
        assertEquals("Tom", setDocument.get("display_name"));
        assertEquals(18, setDocument.get("age_value"));
        assertFalse(setDocument.containsKey("identifier"));
        assertFalse(setDocument.containsKey("email_address"));
    }

    @Test
    @DisplayName("Given metadata resolver When id value missing Then buildIdQuery returns null")
    @Story("Return null when id value missing")
    void givenMetadataResolver_whenIdValueMissing_thenBuildIdQueryReturnsNull() {
        EntityResolver.setEntityMetadataResolver(this.metadataResolver());
        TestExecutor executor = new TestExecutor(wrapper -> new Query());
        MetadataEntity entity = new MetadataEntity(null, "Tom", "t@example.com", 18);

        Query query = executor.exposeBuildIdQuery(entity);

        assertNull(query);
    }

    @Test
    @DisplayName(
        "Given query operations, when executing through the template executor, then delegates to "
            + "the configured mongo template"
    )
    @Story("Delegate query operations to MongoTemplate")
    void givenQueryOperations_whenExecutingThroughTheTemplateExecutor_thenDelegatesToTheConfiguredMongoTemplate() {
        // Given
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        RecordingQueryCompiler queryCompiler = new RecordingQueryCompiler();
        TestExecutor executor = new TestExecutor(queryCompiler);
        QueryWrapper<MetadataEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", "Tom");
        executor.setMongoTemplate(mongoTemplate);
        when(mongoTemplate.find(queryCompiler.queryToReturn, MetadataEntity.class)).thenReturn(List.of());
        when(mongoTemplate.findOne(queryCompiler.queryToReturn, MetadataEntity.class)).thenReturn(null);
        when(mongoTemplate.count(queryCompiler.queryToReturn, "users")).thenReturn(3L);
        when(mongoTemplate.exists(queryCompiler.queryToReturn, MetadataEntity.class)).thenReturn(true);
        when(mongoTemplate.findById(7L, MetadataEntity.class)).thenReturn(null);

        // When
        executor.selectList(wrapper, MetadataEntity.class);
        executor.selectOne(wrapper, MetadataEntity.class);
        long count = executor.selectCount(wrapper, "users");
        boolean exists = executor.exists(wrapper, MetadataEntity.class);
        boolean deleted = executor.delete(wrapper, MetadataEntity.class);
        MetadataEntity byId = executor.selectById(7L, MetadataEntity.class);
        boolean inserted = executor.insert(new MetadataEntity(7L, "Tom", null, 18), "users");

        // Then
        assertEquals(3L, count);
        assertTrue(exists);
        assertTrue(deleted);
        assertNull(byId);
        assertTrue(inserted);
        verify(mongoTemplate).find(queryCompiler.queryToReturn, MetadataEntity.class);
        verify(mongoTemplate).findOne(queryCompiler.queryToReturn, MetadataEntity.class);
        verify(mongoTemplate).count(queryCompiler.queryToReturn, "users");
        verify(mongoTemplate).exists(queryCompiler.queryToReturn, MetadataEntity.class);
        verify(mongoTemplate).remove(queryCompiler.queryToReturn, MetadataEntity.class);
        verify(mongoTemplate).findById(7L, MetadataEntity.class);
        verify(mongoTemplate).insert(any(MetadataEntity.class), eq("users"));
    }

    @Test
    @DisplayName(
        "Given updateById and update wrapper scenarios, when executing the template executor, "
            + "then handles missing identifiers updates and matches correctly"
    )
    @Story("Handle update by id and update wrapper scenarios")
    void givenUpdateScenarios_whenExecutingTheTemplateExecutor_thenHandlesMissingIdentifiersAndMatches(
    ) {
        // Given
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        TestExecutor executor = new TestExecutor(wrapper -> new Query());
        UpdateResult updateResult = mock(UpdateResult.class);
        EntityResolver.setEntityMetadataResolver(this.metadataResolver());
        executor.setMongoTemplate(mongoTemplate);
        when(updateResult.getModifiedCount()).thenReturn(1L);
        when(updateResult.getMatchedCount()).thenReturn(1L);
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq("users"))).thenReturn(updateResult);
        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), eq(MetadataEntity.class)))
            .thenReturn(updateResult);
        MetadataEntity missingId = new MetadataEntity(null, "Tom", null, 18);
        MetadataEntity onlyId = new MetadataEntity(7L, null, null, null);
        MetadataEntity updatable = new MetadataEntity(7L, "Tom", null, 18);
        UpdateWrapper<MetadataEntity> emptyWrapper = new UpdateWrapper<>();
        UpdateWrapper<MetadataEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", "READY");

        // When
        boolean missingIdUpdated = executor.updateById(missingId, "users");
        boolean emptyUpdate = executor.updateById(onlyId, "users");
        boolean updatedById = executor.updateById(updatable, "users");
        boolean wrapperWithoutUpdate = executor.update(emptyWrapper, MetadataEntity.class);
        boolean wrapperUpdated = executor.update(updateWrapper, MetadataEntity.class);

        // Then
        assertFalse(missingIdUpdated);
        assertFalse(emptyUpdate);
        assertTrue(updatedById);
        assertFalse(wrapperWithoutUpdate);
        assertTrue(wrapperUpdated);
        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq("users"));
        verify(mongoTemplate).updateMulti(any(Query.class), any(Update.class), eq(MetadataEntity.class));
    }

    @Test
    @DisplayName(
        "Given paged and batch operations, when executing the template executor, then handles "
            + "counting bulk success and bulk failure branches"
    )
    @Story("Handle paged and batch operations")
    void givenPagedAndBatchOperations_whenExecutingTheTemplateExecutor_thenHandlesBulkSuccessAndFailure(
    ) {
        // Given
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        TestExecutor executor = new TestExecutor(wrapper -> new Query());
        BulkOperations insertBulk = mock(BulkOperations.class);
        BulkOperations deleteBulk = mock(BulkOperations.class);
        BulkWriteResult bulkWriteResult = mock(BulkWriteResult.class);
        DeleteResult deleteResult = mock(DeleteResult.class);
        QueryWrapper<MetadataEntity> wrapper = new QueryWrapper<>();
        Page<MetadataEntity> countedPage = new Page<>(2, 5, true);
        Page<MetadataEntity> uncountedPage = new Page<>(3, 5, false);
        EntityResolver.setEntityMetadataResolver(this.metadataResolver());
        executor.setMongoTemplate(mongoTemplate);
        when(mongoTemplate.count(any(Query.class), eq(MetadataEntity.class))).thenReturn(11L);
        when(mongoTemplate.find(any(Query.class), eq(MetadataEntity.class)))
            .thenReturn(List.of(new MetadataEntity(7L, "Tom", null, 18)))
            .thenReturn(List.of());
        when(mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, "users")).thenReturn(insertBulk, deleteBulk);
        when(insertBulk.insert(any())).thenReturn(insertBulk);
        when(deleteBulk.remove(any(Query.class))).thenReturn(deleteBulk);
        when(insertBulk.execute()).thenReturn(bulkWriteResult);
        when(deleteBulk.execute()).thenThrow(new IllegalStateException("bulk delete failed"));
        when(bulkWriteResult.getInsertedCount()).thenReturn(2);
        when(bulkWriteResult.getModifiedCount()).thenReturn(1);
        when(bulkWriteResult.getDeletedCount()).thenReturn(0);
        when(bulkWriteResult.getMatchedCount()).thenReturn(3);
        when(bulkWriteResult.wasAcknowledged()).thenReturn(true);
        when(deleteResult.getDeletedCount()).thenReturn(1L);
        when(mongoTemplate.remove(any(Query.class), eq("users"))).thenReturn(deleteResult);

        // When
        Page<MetadataEntity> counted = executor.selectPage(countedPage, wrapper, MetadataEntity.class);
        Page<MetadataEntity> uncounted = executor.selectPage(uncountedPage, wrapper, MetadataEntity.class);
        BatchResult emptyInsert = executor.insertBatch(List.<MetadataEntity>of(), "users");
        BatchResult inserted = executor.insertBatch(List.of(new MetadataEntity(7L, "Tom", null, 18)), "users");
        BatchResult deleted = executor.deleteBatch(List.of(7L), "users");
        boolean deletedById = executor.deleteById(7L, "users");

        // Then
        assertEquals(11L, counted.getTotal());
        assertEquals(3L, counted.getPages());
        assertEquals(1, counted.getRecords().size());
        assertEquals(-1L, uncounted.getTotal());
        assertEquals(3L, uncounted.getPages());
        assertTrue(emptyInsert.isAcknowledged());
        assertEquals(2, inserted.getInsertedCount());
        assertTrue(inserted.isAcknowledged());
        assertFalse(deleted.isAcknowledged());
        assertEquals("bulk delete failed", deleted.getErrors().get(0).getMessage());
        assertTrue(deletedById);
    }

    @Test
    @DisplayName("Given page size larger than Integer max When selecting page Then throws ArithmeticException")
    @Story("Template executor rejects page size beyond Integer range")
    void givenPageSizeLargerThanIntegerMax_whenSelectingPage_thenThrowsArithmeticException() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        TestExecutor executor = new TestExecutor(wrapper -> new Query());
        QueryWrapper<MetadataEntity> wrapper = new QueryWrapper<>();
        Page<MetadataEntity> page = new Page<>(1L, (long) Integer.MAX_VALUE + 1L, true);
        executor.setMongoTemplate(mongoTemplate);
        when(mongoTemplate.count(any(Query.class), eq(MetadataEntity.class))).thenReturn(0L);

        assertThrows(ArithmeticException.class, () -> executor.selectPage(page, wrapper, MetadataEntity.class));
    }

    private EntityMetadataResolver metadataResolver() {
        return entityClass -> {
            EntityMetadata metadata = EntityMetadata.builder()
                .entityClass(entityClass)
                .collectionName("users")
                .idField("identifier")
                .idColumn(ID)
                .build();
            metadata.addFieldMapping("identifier", ID);
            metadata.addFieldMapping("displayName", "display_name");
            metadata.addFieldMapping("emailAddress", "email_address");
            metadata.addFieldMapping("age", "age_value");
            return metadata;
        };
    }

    private static final class TestExecutor extends DefaultMongoTemplateExecutor {

        private TestExecutor(QueryCompiler queryCompiler) {
            super(queryCompiler);
        }

        private <T> Query exposeBuildQuery(QueryWrapper<T> wrapper) {
            return this.buildQuery(wrapper);
        }

        private Query exposeBuildIdQuery(Object entity) {
            return this.buildIdQuery(entity);
        }

        private Update exposeBuildUpdateFromEntity(Object entity) {
            return this.buildUpdateFromEntity(entity);
        }
    }

    private static final class RecordingQueryCompiler implements QueryCompiler {

        private boolean wrapperCompileCalled;
        private QueryWrapper<?> lastWrapper;
        private final Query queryToReturn = new Query(Criteria.where("compiled").is(true));

        @Override
        public Query compile(AbstractWrapper<?> wrapper) {
            this.wrapperCompileCalled = true;
            this.lastWrapper = (QueryWrapper<?>) wrapper;
            return this.queryToReturn;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    private static final class MetadataEntity {

        private final Long identifier;
        private final String displayName;
        private final String emailAddress;
        private final Integer age;
    }
}
