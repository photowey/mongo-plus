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
package io.github.photowey.mongoplus.dsl.condition;

import java.util.Collection;
import java.util.function.Consumer;

import io.github.photowey.mongoplus.core.lambda.SFunction;
import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * ConditionDSL - Defines the common condition-building contract shared by query and update wrappers.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * LambdaQueryWrapper<UserDocument> wrapper = Wrappers.lambdaQuery(UserDocument.class)
 *     .eq(UserDocument::getStatus, 1)
 *     .between(UserDocument::getAge, 18, 35)
 *     .or(nested -> nested.eq(UserDocument::getStatus, 2));
 * }</pre>
 *
 * @param <T> the entity type
 * @param <R> the fluent return type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface ConditionDSL<T, R extends ConditionDSL<T, R>> {

    /**
     * Adds an equality condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return the current DSL instance
     */
    R eq(String field, Object value);

    /**
     * Adds an equality condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R eq(SFunction<T, V> column, Object value);

    /**
     * Adds an equality condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R eq(FieldMetadata<V> field, V value) {
        return this.eq(this.resolveField(field), value);
    }

    /**
     * Adds an inequality condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return the current DSL instance
     */
    R ne(String field, Object value);

    /**
     * Adds an inequality condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R ne(SFunction<T, V> column, Object value);

    /**
     * Adds an inequality condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R ne(FieldMetadata<V> field, V value) {
        return this.ne(this.resolveField(field), value);
    }

    /**
     * Adds a greater-than condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return the current DSL instance
     */
    R gt(String field, Object value);

    /**
     * Adds a greater-than condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R gt(SFunction<T, V> column, Object value);

    /**
     * Adds a greater-than condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R gt(FieldMetadata<V> field, V value) {
        return this.gt(this.resolveField(field), value);
    }

    /**
     * Adds a greater-than-or-equal condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return the current DSL instance
     */
    R gte(String field, Object value);

    /**
     * Adds a greater-than-or-equal condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R gte(SFunction<T, V> column, Object value);

    /**
     * Adds a greater-than-or-equal condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R gte(FieldMetadata<V> field, V value) {
        return this.gte(this.resolveField(field), value);
    }

    /**
     * Adds a less-than condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return the current DSL instance
     */
    R lt(String field, Object value);

    /**
     * Adds a less-than condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R lt(SFunction<T, V> column, Object value);

    /**
     * Adds a less-than condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R lt(FieldMetadata<V> field, V value) {
        return this.lt(this.resolveField(field), value);
    }

    /**
     * Adds a less-than-or-equal condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return the current DSL instance
     */
    R lte(String field, Object value);

    /**
     * Adds a less-than-or-equal condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R lte(SFunction<T, V> column, Object value);

    /**
     * Adds a less-than-or-equal condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R lte(FieldMetadata<V> field, V value) {
        return this.lte(this.resolveField(field), value);
    }

    /**
     * Adds a between condition using a raw field name.
     *
     * @param field the field name
     * @param min   the inclusive lower bound
     * @param max   the inclusive upper bound
     * @return the current DSL instance
     */
    R between(String field, Object min, Object max);

    /**
     * Adds a between condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param min    the inclusive lower bound
     * @param max    the inclusive upper bound
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R between(SFunction<T, V> column, Object min, Object max);

    /**
     * Adds a between condition using field metadata.
     *
     * @param field the field metadata
     * @param min   the inclusive lower bound
     * @param max   the inclusive upper bound
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R between(FieldMetadata<V> field, V min, V max) {
        return this.between(this.resolveField(field), min, max);
    }

    /**
     * Adds an in-condition using a raw field name and a collection of values.
     *
     * @param field  the field name
     * @param values the accepted values
     * @return the current DSL instance
     */
    R in(String field, Collection<?> values);

    /**
     * Adds an in-condition using a lambda field reference and a collection of values.
     *
     * @param column the lambda field reference
     * @param values the accepted values
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R in(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds an in-condition using field metadata and a collection of values.
     *
     * @param field  the field metadata
     * @param values the accepted values
     * @return the current DSL instance
     */
    default R in(FieldMetadata<?> field, Collection<?> values) {
        return this.in(this.resolveField(field), values);
    }

    /**
     * Adds an in-condition using a raw field name and a varargs value list.
     *
     * @param field  the field name
     * @param values the accepted values
     * @return the current DSL instance
     */
    R in(String field, Object... values);

    /**
     * Adds an in-condition using a lambda field reference and a varargs value list.
     *
     * @param column the lambda field reference
     * @param values the accepted values
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R in(SFunction<T, V> column, Object... values);

    /**
     * Adds an in-condition using field metadata and a varargs value list.
     *
     * @param field  the field metadata
     * @param values the accepted values
     * @return the current DSL instance
     */
    default R in(FieldMetadata<?> field, Object... values) {
        return this.in(this.resolveField(field), values);
    }

    /**
     * Adds a not-in condition using a raw field name.
     *
     * @param field  the field name
     * @param values the rejected values
     * @return the current DSL instance
     */
    R notIn(String field, Collection<?> values);

    /**
     * Adds a not-in condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param values the rejected values
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R notIn(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds a not-in condition using field metadata.
     *
     * @param field  the field metadata
     * @param values the rejected values
     * @return the current DSL instance
     */
    default R notIn(FieldMetadata<?> field, Collection<?> values) {
        return this.notIn(this.resolveField(field), values);
    }

    /**
     * Alias for {@link #notIn(String, Collection)}.
     *
     * @param field  the field name
     * @param values the rejected values
     * @return the current DSL instance
     */
    default R nin(String field, Collection<?> values) {
        return this.notIn(field, values);
    }

    /**
     * Alias for {@link #notIn(SFunction, Collection)}.
     *
     * @param column the lambda field reference
     * @param values the rejected values
     * @param <V>    the field type
     * @return the current DSL instance
     */
    default <V> R nin(SFunction<T, V> column, Collection<?> values) {
        return this.notIn(column, values);
    }

    /**
     * Alias for {@link #notIn(FieldMetadata, Collection)}.
     *
     * @param field  the field metadata
     * @param values the rejected values
     * @return the current DSL instance
     */
    default R nin(FieldMetadata<?> field, Collection<?> values) {
        return this.notIn(field, values);
    }

    /**
     * Adds a like condition using a raw field name.
     *
     * @param field the field name
     * @param value the pattern source value
     * @return the current DSL instance
     */
    R like(String field, Object value);

    /**
     * Adds a like condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the pattern source value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R like(SFunction<T, V> column, Object value);

    /**
     * Adds a like condition using field metadata.
     *
     * @param field the field metadata
     * @param value the pattern source value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R like(FieldMetadata<V> field, V value) {
        return this.like(this.resolveField(field), value);
    }

    /**
     * Adds a left-like condition using a raw field name.
     *
     * @param field the field name
     * @param value the pattern source value
     * @return the current DSL instance
     */
    R likeLeft(String field, Object value);

    /**
     * Adds a left-like condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the pattern source value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R likeLeft(SFunction<T, V> column, Object value);

    /**
     * Adds a left-like condition using field metadata.
     *
     * @param field the field metadata
     * @param value the pattern source value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R likeLeft(FieldMetadata<V> field, V value) {
        return this.likeLeft(this.resolveField(field), value);
    }

    /**
     * Adds a right-like condition using a raw field name.
     *
     * @param field the field name
     * @param value the pattern source value
     * @return the current DSL instance
     */
    R likeRight(String field, Object value);

    /**
     * Adds a right-like condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the pattern source value
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R likeRight(SFunction<T, V> column, Object value);

    /**
     * Adds a right-like condition using field metadata.
     *
     * @param field the field metadata
     * @param value the pattern source value
     * @param <V>   the field type
     * @return the current DSL instance
     */
    default <V> R likeRight(FieldMetadata<V> field, V value) {
        return this.likeRight(this.resolveField(field), value);
    }

    /**
     * Adds an is-null condition using a raw field name.
     *
     * @param field the field name
     * @return the current DSL instance
     */
    R isNull(String field);

    /**
     * Adds an is-null condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R isNull(SFunction<T, V> column);

    /**
     * Adds an is-null condition using field metadata.
     *
     * @param field the field metadata
     * @return the current DSL instance
     */
    default R isNull(FieldMetadata<?> field) {
        return this.isNull(this.resolveField(field));
    }

    /**
     * Adds an is-not-null condition using a raw field name.
     *
     * @param field the field name
     * @return the current DSL instance
     */
    R isNotNull(String field);

    /**
     * Adds an is-not-null condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R isNotNull(SFunction<T, V> column);

    /**
     * Adds an is-not-null condition using field metadata.
     *
     * @param field the field metadata
     * @return the current DSL instance
     */
    default R isNotNull(FieldMetadata<?> field) {
        return this.isNotNull(this.resolveField(field));
    }

    /**
     * Adds a nested conjunction block.
     *
     * @param consumer the callback that fills the nested block
     * @return the current DSL instance
     */
    R and(Consumer<R> consumer);

    /**
     * Adds a nested disjunction block.
     *
     * @param consumer the callback that fills the nested block
     * @return the current DSL instance
     */
    R or(Consumer<R> consumer);

    /**
     * Adds an exists condition using a raw field name.
     *
     * @param field  the field name
     * @param exists the expected existence flag
     * @return the current DSL instance
     */
    R exists(String field, boolean exists);

    /**
     * Adds an exists condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param exists the expected existence flag
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R exists(SFunction<T, V> column, boolean exists);

    /**
     * Adds an exists condition using field metadata.
     *
     * @param field  the field metadata
     * @param exists the expected existence flag
     * @return the current DSL instance
     */
    default R exists(FieldMetadata<?> field, boolean exists) {
        return this.exists(this.resolveField(field), exists);
    }

    /**
     * Adds an all-condition using a raw field name.
     *
     * @param field  the field name
     * @param values the values that must all be present
     * @return the current DSL instance
     */
    R all(String field, Collection<?> values);

    /**
     * Adds an all-condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param values the values that must all be present
     * @param <V>    the field type
     * @return the current DSL instance
     */
    <V> R all(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds an all-condition using field metadata.
     *
     * @param field  the field metadata
     * @param values the values that must all be present
     * @return the current DSL instance
     */
    default R all(FieldMetadata<?> field, Collection<?> values) {
        return this.all(this.resolveField(field), values);
    }

    /**
     * Resolves a field path from metadata.
     *
     * @param field the field metadata
     * @return the resolved field path
     */
    private String resolveField(FieldMetadata<?> field) {
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("Field metadata must not be null");
        }

        return field.resolvePath();
    }
}
