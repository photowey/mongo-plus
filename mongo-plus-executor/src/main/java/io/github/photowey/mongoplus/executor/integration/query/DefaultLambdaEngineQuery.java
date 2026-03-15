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

import io.github.photowey.mongoplus.core.lambda.SFunction;
import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;

/**
 * DefaultLambdaEngineQuery - Lambda-oriented executable query implementation.
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public class DefaultLambdaEngineQuery<T>
    extends AbstractEngineQuery<T, LambdaQueryWrapper<T>, LambdaEngineQuery<T>>
    implements LambdaEngineQuery<T> {

    /**
     * Creates a lambda query facade backed by the supplied executor and entity class.
     *
     * @param queryExecutor the query executor used by terminal operations
     * @param entityClass   the entity class associated with this query facade
     */
    public DefaultLambdaEngineQuery(QueryExecutor queryExecutor, Class<T> entityClass) {
        this(queryExecutor, entityClass, DefaultLambdaEngineQuery.newWrapper(entityClass));
    }

    /**
     * Creates a lambda query facade around an existing wrapper.
     *
     * @param queryExecutor the query executor used by terminal operations
     * @param entityClass   the entity class associated with this query facade
     * @param wrapper       the wrapper carrying the query state
     */
    private DefaultLambdaEngineQuery(
        QueryExecutor queryExecutor,
        Class<T> entityClass,
        LambdaQueryWrapper<T> wrapper
    ) {
        super(queryExecutor, entityClass, wrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LambdaEngineQuery<T> self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LambdaEngineQuery<T> newQuery(LambdaQueryWrapper<T> wrapper) {
        return new DefaultLambdaEngineQuery<>(this.queryExecutor, this.entityClass, wrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LambdaQueryWrapper<T> castWrapper(AbstractWrapper<T> wrapper) {
        return (LambdaQueryWrapper<T>) wrapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> eq(SFunction<T, V> column, Object value) {
        this.wrapper.eq(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> ne(SFunction<T, V> column, Object value) {
        this.wrapper.ne(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> gt(SFunction<T, V> column, Object value) {
        this.wrapper.gt(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> gte(SFunction<T, V> column, Object value) {
        this.wrapper.gte(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> lt(SFunction<T, V> column, Object value) {
        this.wrapper.lt(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> lte(SFunction<T, V> column, Object value) {
        this.wrapper.lte(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> between(SFunction<T, V> column, Object min, Object max) {
        this.wrapper.between(column, min, max);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> in(SFunction<T, V> column, Collection<?> values) {
        this.wrapper.in(column, values);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> in(SFunction<T, V> column, Object... values) {
        this.wrapper.in(column, values);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> notIn(SFunction<T, V> column, Collection<?> values) {
        this.wrapper.notIn(column, values);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> nin(SFunction<T, V> column, Collection<?> values) {
        this.wrapper.nin(column, values);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> like(SFunction<T, V> column, Object value) {
        this.wrapper.like(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> likeLeft(SFunction<T, V> column, Object value) {
        this.wrapper.likeLeft(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> likeRight(SFunction<T, V> column, Object value) {
        this.wrapper.likeRight(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> regex(SFunction<T, V> column, String pattern) {
        this.wrapper.regex(column, pattern);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> isNull(SFunction<T, V> column) {
        this.wrapper.isNull(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> isNotNull(SFunction<T, V> column) {
        this.wrapper.isNotNull(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> exists(SFunction<T, V> column, boolean exists) {
        this.wrapper.exists(column, exists);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> all(SFunction<T, V> column, Collection<?> values) {
        this.wrapper.all(column, values);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> size(SFunction<T, V> column, int size) {
        this.wrapper.size(column, size);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> orderByAsc(SFunction<T, V> column) {
        this.wrapper.orderByAsc(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> orderByDesc(SFunction<T, V> column) {
        this.wrapper.orderByDesc(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaEngineQuery<T> orderBy(SFunction<T, V> column, boolean ascending) {
        this.wrapper.orderBy(column, ascending);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <V> LambdaEngineQuery<T> select(SFunction<T, V>... columns) {
        this.wrapper.select(columns);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <V> LambdaEngineQuery<T> exclude(SFunction<T, V>... columns) {
        this.wrapper.exclude(columns);
        return this;
    }

    /**
     * Creates a fresh lambda wrapper bound to the supplied entity class.
     *
     * @param entityClass the entity class to bind
     * @param <T>         the entity type
     * @return the bound wrapper
     */
    private static <T> LambdaQueryWrapper<T> newWrapper(Class<T> entityClass) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntityClass(entityClass);
        return wrapper;
    }
}



