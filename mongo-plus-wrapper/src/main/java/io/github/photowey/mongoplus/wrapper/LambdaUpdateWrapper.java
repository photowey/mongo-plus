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
 * LambdaUpdateWrapper - Provides a type-safe update wrapper based on lambda field references.
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public class LambdaUpdateWrapper<T> extends UpdateWrapper<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected LambdaUpdateWrapper<T> instance() {
        return new LambdaUpdateWrapper<>();
    }

    /**
     * Sets a field to the supplied value using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the value to assign
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaUpdateWrapper<T> set(SFunction<T, R> column, Object value) {
        super.set(LambdaUtils.resolve(column), value);
        return this;
    }

    /**
     * Sets a field to {@code null} using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaUpdateWrapper<T> setNull(SFunction<T, R> column) {
        super.setNull(LambdaUtils.resolve(column));
        return this;
    }

    /**
     * Increments a numeric field using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the increment amount
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaUpdateWrapper<T> inc(SFunction<T, R> column, Number value) {
        super.inc(LambdaUtils.resolve(column), value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> eq(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.eq(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> ne(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.ne(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> gt(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.gt(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> gte(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.gte(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> lt(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.lt(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> lte(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.lte(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> in(SFunction<T, R> column, Collection<?> values) {
        return (LambdaUpdateWrapper<T>) super.in(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> notIn(SFunction<T, R> column, Collection<?> values) {
        return (LambdaUpdateWrapper<T>) super.notIn(LambdaUtils.resolve(column), values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> LambdaUpdateWrapper<T> nin(SFunction<T, V> column, Collection<?> values) {
        return (LambdaUpdateWrapper<T>) super.nin(column, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> between(SFunction<T, R> column, Object from, Object to) {
        return (LambdaUpdateWrapper<T>) super.between(LambdaUtils.resolve(column), from, to);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> like(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.like(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> likeLeft(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.likeLeft(LambdaUtils.resolve(column), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> likeRight(SFunction<T, R> column, Object value) {
        return (LambdaUpdateWrapper<T>) super.likeRight(LambdaUtils.resolve(column), value);
    }

    /**
     * Adds a regex condition using a lambda field reference.
     *
     * @param column  the lambda field reference
     * @param pattern the regex pattern
     * @param <R>     the field type
     * @return this wrapper
     */
    public <R> LambdaUpdateWrapper<T> regex(SFunction<T, R> column, String pattern) {
        return (LambdaUpdateWrapper<T>) super.regex(LambdaUtils.resolve(column), pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> exists(SFunction<T, R> column, boolean exists) {
        return (LambdaUpdateWrapper<T>) super.exists(LambdaUtils.resolve(column), exists);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> all(SFunction<T, R> column, Collection<?> values) {
        return (LambdaUpdateWrapper<T>) super.all(LambdaUtils.resolve(column), values);
    }

    /**
     * Adds a size condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param size   the expected collection size
     * @param <R>    the field type
     * @return this wrapper
     */
    public <R> LambdaUpdateWrapper<T> size(SFunction<T, R> column, int size) {
        return (LambdaUpdateWrapper<T>) super.size(LambdaUtils.resolve(column), size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaUpdateWrapper<T> paginate(long pageNum, long pageSize) {
        return (LambdaUpdateWrapper<T>) super.paginate(pageNum, pageSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaUpdateWrapper<T> and(Consumer<AbstractWrapper<T>> consumer) {
        LambdaUpdateWrapper<T> nested = new LambdaUpdateWrapper<>();
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
    public LambdaUpdateWrapper<T> or(Consumer<AbstractWrapper<T>> consumer) {
        LambdaUpdateWrapper<T> nested = new LambdaUpdateWrapper<>();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> isNull(SFunction<T, R> column) {
        return (LambdaUpdateWrapper<T>) super.isNull(LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> LambdaUpdateWrapper<T> isNotNull(SFunction<T, R> column) {
        return (LambdaUpdateWrapper<T>) super.isNotNull(LambdaUtils.resolve(column));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LambdaUpdateWrapper<T> clone() {
        LambdaUpdateWrapper<T> cloned = new LambdaUpdateWrapper<>();
        this.copyStateTo(cloned);
        cloned.setValues().putAll(this.getSetValues());
        cloned.incValues().putAll(this.getIncValues());

        return cloned;
    }
}
