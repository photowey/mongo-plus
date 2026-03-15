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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.metadata.EntityResolver;
import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;

/**
 * AbstractEngineQuery - Shared executable query behavior backed by an {@link AbstractWrapper}.
 *
 * @param <T> the entity type
 * @param <W> the wrapper type
 * @param <S> the self type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
abstract class AbstractEngineQuery<T, W extends AbstractWrapper<T>, S extends EngineQuery<T>>
    implements EngineQuery<T> {

    private static final long DEFAULT_PAGE_CURRENT = 1L;
    private static final long DEFAULT_PAGE_SIZE = 10L;

    protected final QueryExecutor queryExecutor;
    protected final Class<T> entityClass;
    protected final W wrapper;

    /**
     * Creates a support object backed by the supplied executor and wrapper.
     *
     * @param queryExecutor the query executor used by terminal operations
     * @param entityClass   the entity class associated with the wrapper
     * @param wrapper       the underlying wrapper carrying the query state
     */
    protected AbstractEngineQuery(QueryExecutor queryExecutor, Class<T> entityClass, W wrapper) {
        this.queryExecutor = queryExecutor;
        this.entityClass = entityClass;
        this.wrapper = wrapper;
    }

    /**
     * Returns the concrete self type for fluent chaining.
     *
     * @return the concrete query facade instance
     */
    protected abstract S self();

    /**
     * Creates a sibling query facade around a nested wrapper.
     *
     * @param wrapper the nested wrapper instance
     * @return the new query facade
     */
    protected abstract S newQuery(W wrapper);

    /**
     * Casts a generic wrapper to the concrete wrapper type used by this support class.
     *
     * @param wrapper the wrapper to cast
     * @return the concrete wrapper type
     */
    protected abstract W castWrapper(AbstractWrapper<T> wrapper);

    /**
     * {@inheritDoc}
     */
    @Override
    public S eq(String field, Object value) {
        this.wrapper.eq(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S eq(FieldMetadata<V> field, V value) {
        this.wrapper.eq(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S ne(String field, Object value) {
        this.wrapper.ne(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S ne(FieldMetadata<V> field, V value) {
        this.wrapper.ne(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S gt(String field, Object value) {
        this.wrapper.gt(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S gt(FieldMetadata<V> field, V value) {
        this.wrapper.gt(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S gte(String field, Object value) {
        this.wrapper.gte(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S gte(FieldMetadata<V> field, V value) {
        this.wrapper.gte(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S lt(String field, Object value) {
        this.wrapper.lt(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S lt(FieldMetadata<V> field, V value) {
        this.wrapper.lt(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S lte(String field, Object value) {
        this.wrapper.lte(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S lte(FieldMetadata<V> field, V value) {
        this.wrapper.lte(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S between(String field, Object min, Object max) {
        this.wrapper.between(field, min, max);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S between(FieldMetadata<V> field, V min, V max) {
        this.wrapper.between(field, min, max);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S in(String field, Collection<?> values) {
        this.wrapper.in(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S in(String field, Object... values) {
        this.wrapper.in(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S in(FieldMetadata<?> field, Collection<?> values) {
        this.wrapper.in(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S in(FieldMetadata<?> field, Object... values) {
        this.wrapper.in(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S notIn(String field, Collection<?> values) {
        this.wrapper.notIn(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S notIn(FieldMetadata<?> field, Collection<?> values) {
        this.wrapper.notIn(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S nin(String field, Collection<?> values) {
        this.wrapper.nin(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S nin(FieldMetadata<?> field, Collection<?> values) {
        this.wrapper.nin(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S like(String field, Object value) {
        this.wrapper.like(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S like(FieldMetadata<V> field, V value) {
        this.wrapper.like(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S likeLeft(String field, Object value) {
        this.wrapper.likeLeft(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S likeLeft(FieldMetadata<V> field, V value) {
        this.wrapper.likeLeft(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S likeRight(String field, Object value) {
        this.wrapper.likeRight(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> S likeRight(FieldMetadata<V> field, V value) {
        this.wrapper.likeRight(field, value);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S regex(String field, String pattern) {
        this.wrapper.regex(field, pattern);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S regex(FieldMetadata<?> field, String pattern) {
        this.wrapper.regex(field, pattern);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S isNull(String field) {
        this.wrapper.isNull(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S isNull(FieldMetadata<?> field) {
        this.wrapper.isNull(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S isNotNull(String field) {
        this.wrapper.isNotNull(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S isNotNull(FieldMetadata<?> field) {
        this.wrapper.isNotNull(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S exists(String field, boolean exists) {
        this.wrapper.exists(field, exists);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S exists(FieldMetadata<?> field, boolean exists) {
        this.wrapper.exists(field, exists);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists() {
        return this.queryExecutor.exists(this.wrapper, this.entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S all(String field, Collection<?> values) {
        this.wrapper.all(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S all(FieldMetadata<?> field, Collection<?> values) {
        this.wrapper.all(field, values);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S size(String field, int size) {
        this.wrapper.size(field, size);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S size(FieldMetadata<?> field, int size) {
        this.wrapper.size(field, size);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S select(String... fields) {
        this.wrapper.select(fields);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S select(FieldMetadata<?>... fields) {
        this.wrapper.select(fields);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S exclude(String... fields) {
        this.wrapper.exclude(fields);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S exclude(FieldMetadata<?>... fields) {
        this.wrapper.exclude(fields);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orderByAsc(String field) {
        this.wrapper.orderByAsc(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orderByAsc(FieldMetadata<?> field) {
        this.wrapper.orderByAsc(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orderByDesc(String field) {
        this.wrapper.orderByDesc(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orderByDesc(FieldMetadata<?> field) {
        this.wrapper.orderByDesc(field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orderBy(boolean ascending, String field) {
        this.wrapper.orderBy(ascending, field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S orderBy(boolean ascending, FieldMetadata<?> field) {
        this.wrapper.orderBy(ascending, field);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S limit(long limit) {
        this.wrapper.limit(limit);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S skip(long skip) {
        this.wrapper.skip(skip);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S paginate(long pageNum, long pageSize) {
        this.wrapper.paginate(pageNum, pageSize);
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S and(Consumer<EngineQuery<T>> consumer) {
        this.wrapper.and(nested -> consumer.accept(this.newQuery(this.castWrapper(nested))));
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S or(Consumer<EngineQuery<T>> consumer) {
        this.wrapper.or(nested -> consumer.accept(this.newQuery(this.castWrapper(nested))));
        return this.self();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T one() {
        return this.queryExecutor.selectOne(this.wrapper, this.entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> list() {
        return this.queryExecutor.selectList(this.wrapper, this.entityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return this.queryExecutor.selectCount(
            this.wrapper,
            EntityResolver.resolve(this.entityClass).getCollectionName()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> page() {
        return this.page(DEFAULT_PAGE_CURRENT, DEFAULT_PAGE_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> page(long page) {
        return this.page(page, DEFAULT_PAGE_SIZE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> page(long current, long size) {
        return this.queryExecutor.selectPage(
            new Page<>(current, size),
            this.wrapper,
            this.entityClass
        );
    }
}

