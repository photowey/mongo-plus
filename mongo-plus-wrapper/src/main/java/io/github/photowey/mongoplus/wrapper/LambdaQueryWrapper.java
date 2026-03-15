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
package io.github.photowey.mongoplus.wrapper;

import java.util.Collection;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.lambda.LambdaUtils;
import io.github.photowey.mongoplus.core.lambda.SFunction;
import io.github.photowey.mongoplus.core.util.Collections;
import io.github.photowey.mongoplus.dsl.ast.node.LogicalNode;
import io.github.photowey.mongoplus.wrapper.core.condition.Condition;
import io.github.photowey.mongoplus.wrapper.core.enums.Segment;

/**
 * LambdaQueryWrapper - Provides a type-safe query wrapper based on lambda field references.
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class LambdaQueryWrapper<T> extends AbstractWrapper<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected LambdaQueryWrapper<T> instance() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaQueryWrapper<T> clone() {
        LambdaQueryWrapper<T> cloned = new LambdaQueryWrapper<>();
        this.copyStateTo(cloned);

        return cloned;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> eq(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.eq(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> ne(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.ne(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> gt(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.gt(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> gte(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.gte(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> lt(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.lt(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> lte(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.lte(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> in(SFunction<T, R> column, Collection<?> values) {
        return (LambdaQueryWrapper<T>) super.in(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> in(SFunction<T, R> column, Object... values) {
        return (LambdaQueryWrapper<T>) super.in(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> notIn(SFunction<T, R> column, Collection<?> values) {
        return (LambdaQueryWrapper<T>) super.notIn(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaQueryWrapper<T> nin(SFunction<T, V> column, Collection<?> values) {
        return (LambdaQueryWrapper<T>) super.nin(column, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> between(SFunction<T, R> column, Object from, Object to) {
        return (LambdaQueryWrapper<T>) super.between(LambdaUtils.resolve(column), from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> like(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.like(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> likeLeft(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.likeLeft(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> likeRight(SFunction<T, R> column, Object value) {
        return (LambdaQueryWrapper<T>) super.likeRight(LambdaUtils.resolve(column), value);
    }

    /**
     * Adds a regex condition using a lambda field reference.
     *
     * @param column  the lambda field reference
     * @param pattern the regex pattern
     * @param <R>     the field type
     * @return this wrapper
     */
    public <R> LambdaQueryWrapper<T> regex(SFunction<T, R> column, String pattern) {
        return (LambdaQueryWrapper<T>) super.regex(LambdaUtils.resolve(column), pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> isNull(SFunction<T, R> column) {
        return (LambdaQueryWrapper<T>) super.isNull(LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> isNotNull(SFunction<T, R> column) {
        return (LambdaQueryWrapper<T>) super.isNotNull(LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> exists(SFunction<T, R> column, boolean exists) {
        return (LambdaQueryWrapper<T>) super.exists(LambdaUtils.resolve(column), exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaQueryWrapper<T> all(SFunction<T, R> column, Collection<?> values) {
        return (LambdaQueryWrapper<T>) super.all(LambdaUtils.resolve(column), values);
    }

    /**
     * Adds a size condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param size   the expected collection size
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaQueryWrapper<T> size(SFunction<T, R> column, int size) {
        return (LambdaQueryWrapper<T>) super.size(LambdaUtils.resolve(column), size);
    }

    /**
     * Adds an ascending sort using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaQueryWrapper<T> orderByAsc(SFunction<T, R> column) {
        return (LambdaQueryWrapper<T>) super.orderByAsc(LambdaUtils.resolve(column));
    }

    /**
     * Adds a descending sort using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaQueryWrapper<T> orderByDesc(SFunction<T, R> column) {
        return (LambdaQueryWrapper<T>) super.orderByDesc(LambdaUtils.resolve(column));
    }

    /**
     * Restricts the selected fields using lambda field references.
     *
     * @param columns the included field references
     * @param <R>     the field type
     * @return this wrapper
     */
    @SafeVarargs
    public final <R> LambdaQueryWrapper<T> select(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            super.select(LambdaUtils.resolve(column));
        }

        return this;
    }

    /**
     * Excludes fields from the selected projection using lambda field references.
     *
     * @param columns the excluded field references
     * @param <R>     the field type
     * @return this wrapper
     */
    @SafeVarargs
    public final <R> LambdaQueryWrapper<T> exclude(SFunction<T, R>... columns) {
        for (SFunction<T, R> column : columns) {
            super.exclude(LambdaUtils.resolve(column));
        }

        return this;
    }

    /**
     * Adds a sort definition using a lambda field reference.
     *
     * @param column    the lambda field reference
     * @param ascending whether the sort should be ascending
     * @param <R>       the field type
     * @return this wrapper
     */
    public <R> LambdaQueryWrapper<T> orderBy(SFunction<T, R> column, boolean ascending) {
        return (LambdaQueryWrapper<T>) super.orderBy(ascending, LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaQueryWrapper<T> paginate(long pageNum, long pageSize) {
        return (LambdaQueryWrapper<T>) super.paginate(pageNum, pageSize);
    }

    /**
     * Applies page-number based pagination settings.
     *
     * @param pageNum  the current page number, starting from {@code 1}
     * @param pageSize the page size
     * @return this wrapper
     */
    public LambdaQueryWrapper<T> page(long pageNum, long pageSize) {
        return (LambdaQueryWrapper<T>) super.page(pageNum, pageSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaQueryWrapper<T> and(Consumer<AbstractWrapper<T>> consumer) {
        LambdaQueryWrapper<T> nested = new LambdaQueryWrapper<>();
        consumer.accept(nested);
        this.conditions.add(
            Condition.builder()
                .segment(Segment.AND)
                .nestedWrapper(nested)
                .build()
        );
        if (Collections.isNotEmpty(nested.currentLogical.getChildren())) {
            this.currentLogical.addChild(nested.currentLogical);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaQueryWrapper<T> or(Consumer<AbstractWrapper<T>> consumer) {
        LambdaQueryWrapper<T> nested = new LambdaQueryWrapper<>();
        consumer.accept(nested);
        this.conditions.add(
            Condition.builder()
                .segment(Segment.OR)
                .nestedWrapper(nested)
                .build()
        );
        LogicalNode orNode = LogicalNode.or();
        if (Collections.isNotEmpty(nested.currentLogical.getChildren())) {
            orNode.getChildren().addAll(nested.currentLogical.getChildren());
        }
        this.currentLogical.addChild(orNode);

        return this;
    }
}
