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
package io.github.photowey.mongoplus.executor.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.executor.integration.service.DefaultQueryService;
import io.github.photowey.mongoplus.plugin.context.QueryExecutionContext;
import io.github.photowey.mongoplus.plugin.enums.QueryExecutionOperation;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.MongoPlusInterceptor;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;

/**
 * InterceptableQueryExecutorTest - Tests query execution interception behavior.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
@ExtendWith(AllureJunit5.class)
@Epic("MongoPlus")
@Feature("InterceptableQueryExecutorTest")
class InterceptableQueryExecutorTest {

    @Test
    @DisplayName("Given query interception, when selecting a list, then interceptor receives wrapper context")
    @Story("Interceptor receives wrapper context on select list")
    void givenQueryInterception_whenSelectingList_thenInterceptorReceivesWrapperContext() {
        // Given
        RecordingQueryExecutor delegate = new RecordingQueryExecutor();
        List<QueryExecutionContext> contexts = new ArrayList<>();
        MongoPlusInterceptor interceptor = invocation -> {
            contexts.add((QueryExecutionContext) invocation.getContext());
            return invocation.proceed();
        };
        InterceptableQueryExecutor executor = new InterceptableQueryExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );
        AbstractWrapper<UserDocument> wrapper = new QueryWrapper<>();
        wrapper.setEntityClass(UserDocument.class);

        // When
        List<UserDocument> result = executor.selectList(wrapper, UserDocument.class);

        // Then
        Assertions.assertSame(delegate.selectListResult, result);
        Assertions.assertEquals(1, contexts.size());
        Assertions.assertSame(wrapper, contexts.get(0).getWrapper());
        Assertions.assertSame(UserDocument.class, contexts.get(0).getEntityClass());
        Assertions.assertEquals(1, delegate.selectListCalls.get());
    }

    @Test
    @DisplayName("Given short-circuit query interceptor, when counting, then delegate is skipped")
    @Story("Short-circuit query interceptor skips delegate on count")
    void givenShortCircuitQueryInterceptor_whenCounting_thenDelegateIsSkipped() {
        // Given
        RecordingQueryExecutor delegate = new RecordingQueryExecutor();
        MongoPlusInterceptor interceptor = invocation -> 88L;
        InterceptableQueryExecutor executor = new InterceptableQueryExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );

        // When
        long result = executor.selectCount(new QueryWrapper<>(), "users");

        // Then
        Assertions.assertEquals(88L, result);
        Assertions.assertEquals(0, delegate.selectCountCalls.get());
    }

    @Test
    @DisplayName(
        "Given query service with interceptable executor, when listing, then engine query "
            + "execution uses interceptors"
    )
    @Story("Engine query execution uses interceptors for listing")
    void givenQueryServiceWithInterceptableExecutor_whenListing_thenEngineQueryExecutionUsesInterceptors() {
        // Given
        RecordingQueryExecutor delegate = new RecordingQueryExecutor();
        AtomicInteger interceptorCalls = new AtomicInteger();
        MongoPlusInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };
        InterceptableQueryExecutor executor = new InterceptableQueryExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );
        DefaultQueryService queryService = new DefaultQueryService(executor);

        // When
        queryService.createQuery(UserDocument.class)
            .eq("userName", "photowey")
            .list();

        // Then
        Assertions.assertEquals(1, interceptorCalls.get());
        Assertions.assertEquals(1, delegate.selectListCalls.get());
    }

    @Test
    @DisplayName(
        "Given the remaining query executor operations, when invoking them, then interceptors "
            + "receive the correct operation metadata and delegates are called"
    )
    @Story("Interceptor registry integration")
    void givenRemainingQueryExecutorOperations_whenInvokingThem_thenInterceptorsReceiveCorrectMetadata(
    ) {
        // Given
        RecordingQueryExecutor delegate = new RecordingQueryExecutor();
        List<QueryExecutionContext> contexts = new ArrayList<>();
        MongoPlusInterceptor interceptor = invocation -> {
            contexts.add((QueryExecutionContext) invocation.getContext());
            return invocation.proceed();
        };
        InterceptableQueryExecutor executor = new InterceptableQueryExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );
        QueryWrapper<UserDocument> wrapper = new QueryWrapper<>();
        UpdateWrapper<UserDocument> updateWrapper = new UpdateWrapper<>();
        Page<UserDocument> page = new Page<>(2, 5);
        UserDocument entity = new UserDocument();
        List<UserDocument> entities = List.of(entity);
        List<Long> ids = List.of(1L, 2L);

        // When
        executor.selectOne(wrapper, UserDocument.class);
        executor.exists(wrapper, UserDocument.class);
        executor.delete(wrapper, UserDocument.class);
        executor.update(updateWrapper, UserDocument.class);
        executor.selectPage(page, wrapper, UserDocument.class);
        executor.selectById(7L, UserDocument.class);
        executor.insert(entity, "users");
        executor.updateById(entity, "users");
        executor.deleteById(9L, "users");
        executor.insertBatch(entities, "users");
        executor.insertBatch(entities, UserDocument.class);
        executor.updateBatch(entities, "users");
        executor.updateBatch(entities, UserDocument.class);
        executor.deleteBatch(ids, "users");
        executor.deleteBatch(ids, UserDocument.class);

        // Then
        Assertions.assertEquals(15, contexts.size());
        Assertions.assertEquals(QueryExecutionOperation.SELECT_ONE, contexts.get(0).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.EXISTS, contexts.get(1).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.DELETE, contexts.get(2).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.UPDATE, contexts.get(3).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.SELECT_PAGE, contexts.get(4).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.SELECT_BY_ID, contexts.get(5).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.INSERT, contexts.get(6).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.UPDATE_BY_ID, contexts.get(7).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.DELETE_BY_ID, contexts.get(8).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.INSERT_BATCH, contexts.get(9).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.INSERT_BATCH, contexts.get(10).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.UPDATE_BATCH, contexts.get(11).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.UPDATE_BATCH, contexts.get(12).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.DELETE_BATCH, contexts.get(13).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.DELETE_BATCH, contexts.get(14).getOperation());
        Assertions.assertSame(UserDocument.class, contexts.get(6).getEntityClass());
        Assertions.assertEquals("users", contexts.get(6).getCollectionName());
        Assertions.assertSame(UserDocument.class, contexts.get(9).getEntityClass());
        Assertions.assertSame(UserDocument.class, contexts.get(10).getEntityClass());
        Assertions.assertEquals("users", contexts.get(13).getCollectionName());
        Assertions.assertEquals(1, delegate.selectOneCalls.get());
        Assertions.assertEquals(1, delegate.existsCalls.get());
        Assertions.assertEquals(1, delegate.deleteCalls.get());
        Assertions.assertEquals(1, delegate.updateCalls.get());
        Assertions.assertEquals(1, delegate.selectPageCalls.get());
        Assertions.assertEquals(1, delegate.selectByIdCalls.get());
        Assertions.assertEquals(1, delegate.insertCalls.get());
        Assertions.assertEquals(1, delegate.updateByIdCalls.get());
        Assertions.assertEquals(1, delegate.deleteByIdCalls.get());
        Assertions.assertEquals(2, delegate.insertBatchCalls.get());
        Assertions.assertEquals(2, delegate.updateBatchCalls.get());
        Assertions.assertEquals(2, delegate.deleteBatchCalls.get());
    }

    @Test
    @DisplayName(
        "Given empty batch entities or a null interceptor registry, when invoking the executor, "
            + "then falls back safely and keeps entity metadata null"
    )
    @Story("Interceptor registry integration")
    void givenEmptyBatchEntitiesOrNullRegistry_whenInvokingExecutor_thenKeepsEntityMetadataNull(
    ) {
        // Given
        RecordingQueryExecutor delegate = new RecordingQueryExecutor();
        List<QueryExecutionContext> contexts = new ArrayList<>();
        MongoPlusInterceptor interceptor = invocation -> {
            contexts.add((QueryExecutionContext) invocation.getContext());
            return invocation.proceed();
        };
        InterceptableQueryExecutor batchExecutor = new InterceptableQueryExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );
        InterceptableQueryExecutor defaultExecutor = new InterceptableQueryExecutor(delegate, null);

        // When
        batchExecutor.insertBatch(List.<UserDocument>of(), "users");
        batchExecutor.updateBatch(List.<UserDocument>of(), "users");
        defaultExecutor.selectOne(new QueryWrapper<>(), UserDocument.class);

        // Then
        Assertions.assertEquals(2, contexts.size());
        Assertions.assertEquals(QueryExecutionOperation.INSERT_BATCH, contexts.get(0).getOperation());
        Assertions.assertEquals(QueryExecutionOperation.UPDATE_BATCH, contexts.get(1).getOperation());
        Assertions.assertNull(contexts.get(0).getEntityClass());
        Assertions.assertNull(contexts.get(1).getEntityClass());
        Assertions.assertEquals(1, delegate.selectOneCalls.get());
        Assertions.assertEquals(1, delegate.insertBatchCalls.get());
        Assertions.assertEquals(1, delegate.updateBatchCalls.get());
    }

    @Test
    @DisplayName(
        "Given a checked throwable interceptor, when invoking the executor, then wraps it in an "
            + "illegal state exception"
    )
    @Story("Wrap checked throwable interceptor in IllegalStateException")
    void givenACheckedThrowableInterceptor_whenInvokingTheExecutor_thenWrapsItInAnIllegalStateException() {
        // Given
        RecordingQueryExecutor delegate = new RecordingQueryExecutor();
        MongoPlusInterceptor interceptor = invocation -> {
            throw new Exception("boom");
        };
        InterceptableQueryExecutor executor = new InterceptableQueryExecutor(
            delegate,
            new InterceptorRegistry(List.of(interceptor))
        );

        // When
        IllegalStateException error = Assertions.assertThrows(
            IllegalStateException.class,
            () -> executor.selectOne(new QueryWrapper<>(), UserDocument.class)
        );

        // Then
        Assertions.assertEquals("Failed to execute query interceptor chain", error.getMessage());
        Assertions.assertEquals("boom", error.getCause().getMessage());
    }

    private static final class UserDocument implements Serializable {

        private static final long serialVersionUID = 1L;
    }

    @SuppressWarnings("all")
    private static final class RecordingQueryExecutor implements QueryExecutor {

        private final AtomicInteger selectListCalls = new AtomicInteger();
        private final AtomicInteger selectOneCalls = new AtomicInteger();
        private final AtomicInteger selectCountCalls = new AtomicInteger();
        private final AtomicInteger existsCalls = new AtomicInteger();
        private final AtomicInteger deleteCalls = new AtomicInteger();
        private final AtomicInteger updateCalls = new AtomicInteger();
        private final AtomicInteger selectPageCalls = new AtomicInteger();
        private final AtomicInteger selectByIdCalls = new AtomicInteger();
        private final AtomicInteger insertCalls = new AtomicInteger();
        private final AtomicInteger updateByIdCalls = new AtomicInteger();
        private final AtomicInteger deleteByIdCalls = new AtomicInteger();
        private final AtomicInteger insertBatchCalls = new AtomicInteger();
        private final AtomicInteger updateBatchCalls = new AtomicInteger();
        private final AtomicInteger deleteBatchCalls = new AtomicInteger();
        private final List<UserDocument> selectListResult = new ArrayList<>();

        @Override
        public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.selectListCalls.incrementAndGet();
            return (List<T>) this.selectListResult;
        }

        @Override
        public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.selectOneCalls.incrementAndGet();
            return null;
        }

        @Override
        public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
            this.selectCountCalls.incrementAndGet();
            return 1L;
        }

        @Override
        public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.existsCalls.incrementAndGet();
            return false;
        }

        @Override
        public <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.deleteCalls.incrementAndGet();
            return false;
        }

        @Override
        public <T> boolean update(UpdateWrapper<T> updateWrapper, Class<T> entityClass) {
            this.updateCalls.incrementAndGet();
            return false;
        }

        @Override
        public <T> Page<T> selectPage(Page<T> page, AbstractWrapper<T> wrapper, Class<T> entityClass) {
            this.selectPageCalls.incrementAndGet();
            return page;
        }

        @Override
        public <T> T selectById(Serializable id, Class<T> entityClass) {
            this.selectByIdCalls.incrementAndGet();
            return null;
        }

        @Override
        public <T> boolean insert(T entity, String collectionName) {
            this.insertCalls.incrementAndGet();
            return false;
        }

        @Override
        public <T> boolean updateById(T entity, String collectionName) {
            this.updateByIdCalls.incrementAndGet();
            return false;
        }

        @Override
        public boolean deleteById(Serializable id, String collectionName) {
            this.deleteByIdCalls.incrementAndGet();
            return false;
        }

        @Override
        public <T> BatchResult insertBatch(List<T> entities, String collectionName) {
            this.insertBatchCalls.incrementAndGet();
            return null;
        }

        @Override
        public <T> BatchResult insertBatch(List<T> entities, Class<T> entityClass) {
            this.insertBatchCalls.incrementAndGet();
            return null;
        }

        @Override
        public <T> BatchResult updateBatch(List<T> entities, String collectionName) {
            this.updateBatchCalls.incrementAndGet();
            return null;
        }

        @Override
        public <T> BatchResult updateBatch(List<T> entities, Class<T> entityClass) {
            this.updateBatchCalls.incrementAndGet();
            return null;
        }

        @Override
        public BatchResult deleteBatch(List<? extends Serializable> ids, String collectionName) {
            this.deleteBatchCalls.incrementAndGet();
            return null;
        }

        @Override
        public <T> BatchResult deleteBatch(List<? extends Serializable> ids, Class<T> entityClass) {
            this.deleteBatchCalls.incrementAndGet();
            return null;
        }
    }
}
