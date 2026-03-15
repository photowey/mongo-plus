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
import java.util.List;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.batch.BatchResult;
import io.github.photowey.mongoplus.plugin.context.QueryExecutionContext;
import io.github.photowey.mongoplus.plugin.enums.QueryExecutionOperation;
import io.github.photowey.mongoplus.plugin.interceptor.InterceptorRegistry;
import io.github.photowey.mongoplus.plugin.interceptor.InvocationTarget;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;

/**
 * InterceptableQueryExecutor - Decorates a {@link QueryExecutor} with execution interceptor support.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/11
 */
public class InterceptableQueryExecutor implements QueryExecutor {

    private final QueryExecutor delegate;
    private final InterceptorRegistry interceptorRegistry;

    /**
     * Creates a decorator around the supplied delegate.
     *
     * @param delegate            the wrapped query executor
     * @param interceptorRegistry the interceptor registry used for execution
     */
    public InterceptableQueryExecutor(
        QueryExecutor delegate,
        InterceptorRegistry interceptorRegistry
    ) {
        this.delegate = delegate;
        this.interceptorRegistry = Objects.nonNull(interceptorRegistry)
            ? interceptorRegistry
            : InterceptorRegistry.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.SELECT_LIST)
                .entityClass(entityClass)
                .arguments(new Object[] {wrapper})
                .build(),
            () -> this.delegate.selectList(wrapper, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.SELECT_ONE)
                .entityClass(entityClass)
                .arguments(new Object[] {wrapper})
                .build(),
            () -> this.delegate.selectOne(wrapper, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long selectCount(AbstractWrapper<?> wrapper, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.SELECT_COUNT)
                .collectionName(collectionName)
                .arguments(new Object[] {wrapper})
                .build(),
            () -> this.delegate.selectCount(wrapper, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.EXISTS)
                .entityClass(entityClass)
                .arguments(new Object[] {wrapper})
                .build(),
            () -> this.delegate.exists(wrapper, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.DELETE)
                .entityClass(entityClass)
                .arguments(new Object[] {wrapper})
                .build(),
            () -> this.delegate.delete(wrapper, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean update(UpdateWrapper<T> updateWrapper, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.UPDATE)
                .entityClass(entityClass)
                .arguments(new Object[] {updateWrapper})
                .build(),
            () -> this.delegate.update(updateWrapper, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Page<T> selectPage(Page<T> page, AbstractWrapper<T> wrapper, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.SELECT_PAGE)
                .entityClass(entityClass)
                .arguments(new Object[] {page, wrapper})
                .build(),
            () -> this.delegate.selectPage(page, wrapper, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T selectById(Serializable id, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.SELECT_BY_ID)
                .entityClass(entityClass)
                .arguments(new Object[] {id})
                .build(),
            () -> this.delegate.selectById(id, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean insert(T entity, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.INSERT)
                .entityClass(this.resolveEntityClass(entity))
                .collectionName(collectionName)
                .arguments(new Object[] {entity})
                .build(),
            () -> this.delegate.insert(entity, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean updateById(T entity, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.UPDATE_BY_ID)
                .entityClass(this.resolveEntityClass(entity))
                .collectionName(collectionName)
                .arguments(new Object[] {entity})
                .build(),
            () -> this.delegate.updateById(entity, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteById(Serializable id, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.DELETE_BY_ID)
                .collectionName(collectionName)
                .arguments(new Object[] {id})
                .build(),
            () -> this.delegate.deleteById(id, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult insertBatch(List<T> entities, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.INSERT_BATCH)
                .entityClass(this.resolveEntityClass(entities))
                .collectionName(collectionName)
                .arguments(new Object[] {entities})
                .build(),
            () -> this.delegate.insertBatch(entities, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult insertBatch(List<T> entities, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.INSERT_BATCH)
                .entityClass(entityClass)
                .arguments(new Object[] {entities})
                .build(),
            () -> this.delegate.insertBatch(entities, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult updateBatch(List<T> entities, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.UPDATE_BATCH)
                .entityClass(this.resolveEntityClass(entities))
                .collectionName(collectionName)
                .arguments(new Object[] {entities})
                .build(),
            () -> this.delegate.updateBatch(entities, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult updateBatch(List<T> entities, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.UPDATE_BATCH)
                .entityClass(entityClass)
                .arguments(new Object[] {entities})
                .build(),
            () -> this.delegate.updateBatch(entities, entityClass)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BatchResult deleteBatch(List<? extends Serializable> ids, String collectionName) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.DELETE_BATCH)
                .collectionName(collectionName)
                .arguments(new Object[] {ids})
                .build(),
            () -> this.delegate.deleteBatch(ids, collectionName)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> BatchResult deleteBatch(List<? extends Serializable> ids, Class<T> entityClass) {
        return this.invoke(
            QueryExecutionContext.builder()
                .operation(QueryExecutionOperation.DELETE_BATCH)
                .entityClass(entityClass)
                .arguments(new Object[] {ids})
                .build(),
            () -> this.delegate.deleteBatch(ids, entityClass)
        );
    }

    @SuppressWarnings("unchecked")
    private <R> R invoke(QueryExecutionContext context, InvocationTarget target) {
        try {
            return (R) this.interceptorRegistry.invoke(context, target);
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to execute query interceptor chain", e);
        }
    }

    private Class<?> resolveEntityClass(Object entity) {
        return Objects.nonNull(entity) ? entity.getClass() : null;
    }

    private Class<?> resolveEntityClass(List<?> entities) {
        if (Collections.isEmpty(entities)) {
            return null;
        }

        return this.resolveEntityClass(entities.get(0));
    }
}
