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
package io.github.photowey.mongoplus.wrapper.core.util;

import java.util.Collection;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.util.AssertionErrors;
import io.github.photowey.mongoplus.core.util.Objects;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaUpdateWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;

/**
 * Wrappers - Exposes factory methods for the framework's query and update wrappers.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * LambdaQueryWrapper<UserDocument> wrapper = Wrappers.lambdaQuery(UserDocument.class)
 *     .eq(UserDocument::getStatus, 1)
 *     .orderByDesc(UserDocument::getId);
 * }</pre>
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
@SuppressWarnings("unchecked")
public final class Wrappers {

    private static final QueryWrapper<?> EMPTY_WRAPPER = new EmptyWrapper<>();

    /**
     * Prevents utility-class instantiation.
     */
    private Wrappers() {
        AssertionErrors.throwz(Wrappers.class);
    }

    /**
     * Creates an untyped {@link QueryWrapper}.
     *
     * @param <T> the entity type
     * @return a new query wrapper
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    /**
     * Creates a {@link QueryWrapper} bound to the supplied entity class.
     *
     * @param entityClass the entity class used for metadata resolution
     * @param <T>         the entity type
     * @return a new query wrapper
     */
    public static <T> QueryWrapper<T> query(Class<T> entityClass) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.setEntityClass(entityClass);

        return wrapper;
    }

    /**
     * Creates a {@link QueryWrapper} using the runtime type of the supplied entity instance.
     *
     * @param entity the entity instance used to infer the entity class
     * @param <T>    the entity type
     * @return a new query wrapper
     */
    public static <T> QueryWrapper<T> query(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (Objects.nonNull(entity)) {
            Class<T> clazz = (Class<T>) entity.getClass();
            wrapper.setEntityClass(clazz);
        }

        return wrapper;
    }

    /**
     * Creates an untyped {@link LambdaQueryWrapper}.
     *
     * @param <T> the entity type
     * @return a new lambda query wrapper
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * Creates a {@link LambdaQueryWrapper} bound to the supplied entity class.
     *
     * @param entityClass the entity class used for metadata resolution
     * @param <T>         the entity type
     * @return a new lambda query wrapper
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntityClass(entityClass);

        return wrapper;
    }

    /**
     * Creates a {@link LambdaQueryWrapper} using the runtime type of the supplied entity instance.
     *
     * @param entity the entity instance used to infer the entity class
     * @param <T>    the entity type
     * @return a new lambda query wrapper
     */
    public static <T> LambdaQueryWrapper<T> lambdaQuery(T entity) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(entity)) {
            Class<T> clazz = (Class<T>) entity.getClass();
            wrapper.setEntityClass(clazz);
        }

        return wrapper;
    }

    /**
     * Creates an untyped {@link UpdateWrapper}.
     *
     * @param <T> the entity type
     * @return a new update wrapper
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * Creates an {@link UpdateWrapper} bound to the supplied entity class.
     *
     * @param entityClass the entity class used for metadata resolution
     * @param <T>         the entity type
     * @return a new update wrapper
     */
    public static <T> UpdateWrapper<T> update(Class<T> entityClass) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        wrapper.setEntityClass(entityClass);

        return wrapper;
    }

    /**
     * Creates an {@link UpdateWrapper} using the runtime type of the supplied entity instance.
     *
     * @param entity the entity instance used to infer the entity class
     * @param <T>    the entity type
     * @return a new update wrapper
     */
    public static <T> UpdateWrapper<T> update(T entity) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>();
        if (Objects.nonNull(entity)) {
            Class<T> clazz = (Class<T>) entity.getClass();
            wrapper.setEntityClass(clazz);
        }

        return wrapper;
    }

    /**
     * Creates an untyped {@link LambdaUpdateWrapper}.
     *
     * @param <T> the entity type
     * @return a new lambda update wrapper
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate() {
        return new LambdaUpdateWrapper<>();
    }

    /**
     * Creates a {@link LambdaUpdateWrapper} bound to the supplied entity class.
     *
     * @param entityClass the entity class used for metadata resolution
     * @param <T>         the entity type
     * @return a new lambda update wrapper
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(Class<T> entityClass) {
        LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setEntityClass(entityClass);

        return wrapper;
    }

    /**
     * Creates a {@link LambdaUpdateWrapper} using the runtime type of the supplied entity instance.
     *
     * @param entity the entity instance used to infer the entity class
     * @param <T>    the entity type
     * @return a new lambda update wrapper
     */
    public static <T> LambdaUpdateWrapper<T> lambdaUpdate(T entity) {
        LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>();
        if (Objects.nonNull(entity)) {
            Class<T> clazz = (Class<T>) entity.getClass();
            wrapper.setEntityClass(clazz);
        }

        return wrapper;
    }

    /**
     * Returns a shared immutable wrapper instance that rejects all mutations.
     *
     * @param <T> the entity type
     * @return the shared immutable empty wrapper
     */
    public static <T> QueryWrapper<T> emptyWrapper() {
        return (QueryWrapper<T>) EMPTY_WRAPPER;
    }

    /**
     * Determines whether the supplied wrapper contains no query or update content.
     *
     * @param wrapper the wrapper to inspect
     * @param <T>     the entity type
     * @return {@code true} when the wrapper is {@code null} or carries no conditions or updates
     */
    public static <T> boolean isEmpty(AbstractWrapper<T> wrapper) {
        if (Objects.isNull(wrapper)) {
            return true;
        }
        if (wrapper instanceof UpdateWrapper) {
            UpdateWrapper<T> updateWrapper = (UpdateWrapper<T>) wrapper;
            return wrapper.getConditions().isEmpty()
                && updateWrapper.getSetValues().isEmpty()
                && updateWrapper.getIncValues().isEmpty();
        }

        return wrapper.getConditions().isEmpty();
    }

    /**
     * Determines whether the supplied wrapper contains at least one condition or update instruction.
     *
     * @param wrapper the wrapper to inspect
     * @param <T>     the entity type
     * @return {@code true} when the wrapper contains query or update content
     */
    public static <T> boolean isNotEmpty(AbstractWrapper<T> wrapper) {
        return !isEmpty(wrapper);
    }

    /**
     * Immutable wrapper implementation used by {@link #emptyWrapper()}.
     *
     * @param <T> the entity type
     * @author photowey
     * @version 2026.1.0.0
     * @since 2026/03/12
     */
    private static final class EmptyWrapper<T> extends QueryWrapper<T> {

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> eq(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> ne(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> gt(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> gte(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> lt(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> lte(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> like(String field, Object value) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> in(String field, Collection<?> values) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> and(Consumer<AbstractWrapper<T>> consumer) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> or(Consumer<AbstractWrapper<T>> consumer) {
            throw new UnsupportedOperationException("EmptyWrapper is immutable");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected QueryWrapper<T> instance() {
            return new QueryWrapper<>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QueryWrapper<T> clone() {
            return new QueryWrapper<>();
        }
    }
}
