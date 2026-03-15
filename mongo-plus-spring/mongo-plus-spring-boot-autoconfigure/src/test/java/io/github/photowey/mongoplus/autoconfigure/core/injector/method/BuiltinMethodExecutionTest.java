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
package io.github.photowey.mongoplus.autoconfigure.core.injector.method;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.aggregation.stage.Pipeline;
import io.github.photowey.mongoplus.autoconfigure.core.proxy.handler.MethodHandlerRegistry;
import io.github.photowey.mongoplus.core.metadata.EntityMetadata;
import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.AggregationExecutor;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.executor.getter.ExecutorGetter;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BuiltinMethodExecutionTest - Unit tests for built-in injector method handler execution.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/14
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("BuiltinMethodExecutionTest")
class BuiltinMethodExecutionTest {

    @AfterEach
    void resetEntityResolver() {
        EntityResolver.resetEntityMetadataResolver();
        EntityResolver.clearCache();
    }

    @Test
    @DisplayName(
        "Given built in injector methods, when invoking their handlers, then delegates to the "
            + "expected query executor operations"
    )
    @Story("Built-in injector methods delegate to query executor operations")
    void givenBuiltInInjectorMethods_whenInvokingTheirHandlers_thenDelegatesToTheExpectedQueryExecutorOperations()
        throws Exception {
        // Given
        EntityResolver.setEntityMetadataResolver(entityClass -> EntityMetadata.builder()
            .entityClass(entityClass)
            .collectionName("users")
            .build());
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        RecordingAggregationExecutor aggregationExecutor = new RecordingAggregationExecutor();
        ExecutorGetter getter = new ExecutorGetter() {
            @Override
            public QueryExecutor queryExecutor() {
                return queryExecutor;
            }

            @Override
            public AggregationExecutor aggregationExecutor() {
                return aggregationExecutor;
            }
        };
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Object> updateWrapper = new UpdateWrapper<>();
        Page<Object> page = new Page<>(2, 5);
        List<Object> entities = List.of("A");
        List<Long> ids = List.of(1L, 2L);

        // When
        registry.getHandler(BuiltinMapper.class.getMethod("selectCount", AbstractWrapper.class))
            .execute(getter, Object.class, new Object[] {queryWrapper});
        registry.getHandler(BuiltinMapper.class.getMethod("exists", AbstractWrapper.class))
            .execute(getter, Object.class, new Object[] {queryWrapper});
        registry.getHandler(BuiltinMapper.class.getMethod("insert", Object.class))
            .execute(getter, Object.class, new Object[] {"entity"});
        registry.getHandler(BuiltinMapper.class.getMethod("selectPage", Page.class, AbstractWrapper.class))
            .execute(getter, Object.class, new Object[] {page, queryWrapper});
        registry.getHandler(BuiltinMapper.class.getMethod("deleteById", Serializable.class))
            .execute(getter, Object.class, new Object[] {7L});
        registry.getHandler(BuiltinMapper.class.getMethod("delete", AbstractWrapper.class))
            .execute(getter, Object.class, new Object[] {queryWrapper});
        registry.getHandler(BuiltinMapper.class.getMethod("insertBatch", List.class))
            .execute(getter, Object.class, new Object[] {entities});
        registry.getHandler(BuiltinMapper.class.getMethod("updateBatch", List.class))
            .execute(getter, Object.class, new Object[] {entities});
        registry.getHandler(BuiltinMapper.class.getMethod("updateById", Object.class))
            .execute(getter, Object.class, new Object[] {"entity"});
        registry.getHandler(BuiltinMapper.class.getMethod("update", UpdateWrapper.class))
            .execute(getter, Object.class, new Object[] {updateWrapper});
        registry.getHandler(BuiltinMapper.class.getMethod("deleteBatch", List.class))
            .execute(getter, Object.class, new Object[] {ids});

        // Then
        assertEquals("users", queryExecutor.lastCollectionName);
        assertSame(queryWrapper, queryExecutor.lastWrapper);
        assertSame(updateWrapper, queryExecutor.lastUpdateWrapper);
        assertSame(page, queryExecutor.lastPage);
        assertSame(entities, queryExecutor.lastEntities);
        assertSame(ids, queryExecutor.lastIds);
        assertEquals(7L, queryExecutor.lastId);
        assertTrue(queryExecutor.insertCalled);
        assertTrue(queryExecutor.updateByIdCalled);
        assertTrue(queryExecutor.deleteCalled);
        assertTrue(queryExecutor.deleteByIdCalled);
    }

    @Test
    @DisplayName(
        "Given selectList and selectOne wrapper variants, when invoking built in handlers, then "
            + "covers query lambda and generic wrapper branches"
    )
    @Story("SelectList and SelectOne handlers cover query lambda and generic branches")
    void givenSelectListAndSelectOneWrapperVariants_whenInvokingBuiltInHandlers_thenCoversQueryLambdaAndGenericBranches()
        throws Exception {
        // Given
        MethodHandlerRegistry registry = new MethodHandlerRegistry();
        RecordingQueryExecutor queryExecutor = new RecordingQueryExecutor();
        ExecutorGetter getter = new ExecutorGetter() {
            @Override
            public QueryExecutor queryExecutor() {
                return queryExecutor;
            }

            @Override
            public AggregationExecutor aggregationExecutor() {
                return new RecordingAggregationExecutor();
            }
        };
        QueryWrapper<Object> queryWrapper = new QueryWrapper<>();
        LambdaQueryWrapper<Object> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        GenericWrapper genericWrapper = new GenericWrapper();

        // When
        Object queryList = registry.getHandler(BuiltinMapper.class.getMethod("selectList", QueryWrapper.class))
            .execute(getter, Object.class, new Object[] {queryWrapper});
        Object lambdaList = registry.getHandler(
            BuiltinMapper.class.getMethod("selectList", LambdaQueryWrapper.class)
        ).execute(getter, Object.class, new Object[] {lambdaQueryWrapper});
        Object genericList = registry.getHandler(
            BuiltinMapper.class.getMethod("selectList", AbstractWrapper.class)
        ).execute(getter, Object.class, new Object[] {genericWrapper});
        Object queryOne = registry.getHandler(BuiltinMapper.class.getMethod("selectOne", QueryWrapper.class))
            .execute(getter, Object.class, new Object[] {queryWrapper});
        Object lambdaOne = registry.getHandler(
            BuiltinMapper.class.getMethod("selectOne", LambdaQueryWrapper.class)
        ).execute(getter, Object.class, new Object[] {lambdaQueryWrapper});
        Object genericOne = registry.getHandler(
            BuiltinMapper.class.getMethod("selectOne", AbstractWrapper.class)
        ).execute(getter, Object.class, new Object[] {genericWrapper});

        // Then
        assertEquals(List.of("listed"), queryList);
        assertEquals(List.of("listed"), lambdaList);
        assertEquals(List.of("listed"), genericList);
        assertEquals("one", queryOne);
        assertEquals("one", lambdaOne);
        assertEquals("one", genericOne);
        assertSame(genericWrapper, queryExecutor.lastWrapper);
    }

    interface BuiltinMapper {

        List<Object> selectList(QueryWrapper<Object> wrapper);

        List<Object> selectList(LambdaQueryWrapper<Object> wrapper);

        List<Object> selectList(AbstractWrapper<Object> wrapper);

        Object selectOne(QueryWrapper<Object> wrapper);

        Object selectOne(LambdaQueryWrapper<Object> wrapper);

        Object selectOne(AbstractWrapper<Object> wrapper);

        long selectCount(AbstractWrapper<Object> wrapper);

        boolean exists(AbstractWrapper<Object> wrapper);

        boolean insert(Object entity);

        Page<Object> selectPage(Page<Object> page, AbstractWrapper<Object> wrapper);

        boolean deleteById(Serializable id);

        boolean delete(AbstractWrapper<Object> wrapper);

        BatchResult insertBatch(List<Object> entities);

        BatchResult updateBatch(List<Object> entities);

        boolean updateById(Object entity);

        boolean update(UpdateWrapper<Object> wrapper);

        BatchResult deleteBatch(List<? extends Serializable> ids);
    }

    private static final class RecordingQueryExecutor implements QueryExecutor {

        private String lastCollectionName;
        private AbstractWrapper<Object> lastWrapper;
        private UpdateWrapper<Object> lastUpdateWrapper;
        private Page<Object> lastPage;
        private List<Object> lastEntities;
        private List<? extends Serializable> lastIds;
        private Serializable lastId;
        private boolean insertCalled;
        private boolean updateByIdCalled;
        private boolean deleteCalled;
        private boolean deleteByIdCalled;

        @Override
        public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<Object>) wrapper;
            return (List<T>) List.of("listed");
        }

        @Override
        public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<Object>) wrapper;
            return (T) "one";
        }

        @SuppressWarnings("unchecked")
        @Override
        public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
            this.lastWrapper = (AbstractWrapper<Object>) wrapper;
            this.lastCollectionName = collectionName;
            return 1L;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<Object>) wrapper;
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastWrapper = (AbstractWrapper<Object>) wrapper;
            this.deleteCalled = true;
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> boolean update(UpdateWrapper<T> updateWrapper, Class<T> entityClass) {
            this.lastUpdateWrapper = (UpdateWrapper<Object>) updateWrapper;
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> Page<T> selectPage(Page<T> page, AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.lastPage = (Page<Object>) page;
            this.lastWrapper = (AbstractWrapper<Object>) wrapper;
            return page;
        }

        @Override
        public <T> T selectById(Serializable id, Class<T> entityClass) {
            return null;
        }

        @Override
        public <T> boolean insert(T entity, String collectionName) {
            this.insertCalled = true;
            this.lastCollectionName = collectionName;
            return true;
        }

        @Override
        public <T> boolean updateById(T entity, String collectionName) {
            this.updateByIdCalled = true;
            this.lastCollectionName = collectionName;
            return true;
        }

        @Override
        public boolean deleteById(Serializable id, String collectionName) {
            this.deleteByIdCalled = true;
            this.lastId = id;
            this.lastCollectionName = collectionName;
            return true;
        }

        @Override
        public <T> BatchResult insertBatch(List<T> entities, String collectionName) {
            this.lastEntities = (List<Object>) entities;
            this.lastCollectionName = collectionName;
            return BatchResult.builder().acknowledged(true).build();
        }

        @Override
        public <T> BatchResult insertBatch(List<T> entities, Class<T> entityClass) {
            this.lastEntities = (List<Object>) entities;
            return BatchResult.builder().acknowledged(true).build();
        }

        @Override
        public <T> BatchResult updateBatch(List<T> entities, String collectionName) {
            this.lastEntities = (List<Object>) entities;
            this.lastCollectionName = collectionName;
            return BatchResult.builder().acknowledged(true).build();
        }

        @Override
        public <T> BatchResult updateBatch(List<T> entities, Class<T> entityClass) {
            this.lastEntities = (List<Object>) entities;
            return BatchResult.builder().acknowledged(true).build();
        }

        @Override
        public BatchResult deleteBatch(List<? extends Serializable> ids, String collectionName) {
            this.lastIds = ids;
            this.lastCollectionName = collectionName;
            return BatchResult.builder().acknowledged(true).build();
        }

        @Override
        public <T> BatchResult deleteBatch(List<? extends Serializable> ids, Class<T> entityClass) {
            this.lastIds = ids;
            return BatchResult.builder().acknowledged(true).build();
        }
    }

    private static final class RecordingAggregationExecutor implements AggregationExecutor {

        @Override
        public <T, R> List<R> execute(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
            return List.of();
        }

        @Override
        public <R> List<R> execute(Pipeline pipeline, String collectionName, Class<R> outputType) {
            return List.of();
        }

        @Override
        public <T, R> List<R> executeRaw(Pipeline pipeline, Class<T> inputType, Class<R> outputType) {
            return List.of();
        }
    }

    private static final class GenericWrapper extends AbstractWrapper<Object> {

        @Override
        protected AbstractWrapper<Object> instance() {
            return new GenericWrapper();
        }

        @Override
        public AbstractWrapper<Object> clone() {
            GenericWrapper cloned = new GenericWrapper();
            this.copyStateTo(cloned);
            return cloned;
        }
    }
}
