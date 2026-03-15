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

/**
 * LambdaEngineQuery - Extends {@link EngineQuery} with lambda-safe field references.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * List<UserDocument> users = lambdaEngineQuery
 *     .eq(UserDocument::getStatus, 1)
 *     .orderByDesc(UserDocument::getCreatedAt)
 *     .list();
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public interface LambdaEngineQuery<T> extends EngineQuery<T> {

    /**
     * Adds an equality condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> eq(SFunction<T, V> column, Object value);

    /**
     * Adds an inequality condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> ne(SFunction<T, V> column, Object value);

    /**
     * Adds a greater-than condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> gt(SFunction<T, V> column, Object value);

    /**
     * Adds a greater-than-or-equal condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> gte(SFunction<T, V> column, Object value);

    /**
     * Adds a less-than condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> lt(SFunction<T, V> column, Object value);

    /**
     * Adds a less-than-or-equal condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the comparison value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> lte(SFunction<T, V> column, Object value);

    /**
     * Adds a between condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param min    the inclusive lower bound
     * @param max    the inclusive upper bound
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> between(SFunction<T, V> column, Object min, Object max);

    /**
     * Adds an in-condition using a lambda field reference and a collection.
     *
     * @param column the lambda field reference
     * @param values the accepted values
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> in(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds an in-condition using a lambda field reference and a varargs value list.
     *
     * @param column the lambda field reference
     * @param values the accepted values
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> in(SFunction<T, V> column, Object... values);

    /**
     * Adds a not-in condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param values the rejected values
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> notIn(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds a not-in alias using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param values the rejected values
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> nin(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds a like condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the pattern source value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> like(SFunction<T, V> column, Object value);

    /**
     * Adds a left-like condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the pattern source value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> likeLeft(SFunction<T, V> column, Object value);

    /**
     * Adds a right-like condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param value  the pattern source value
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> likeRight(SFunction<T, V> column, Object value);

    /**
     * Adds a regex condition using a lambda field reference.
     *
     * @param column  the lambda field reference
     * @param pattern the regex pattern
     * @param <V>     the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> regex(SFunction<T, V> column, String pattern);

    /**
     * Adds an is-null condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> isNull(SFunction<T, V> column);

    /**
     * Adds an is-not-null condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> isNotNull(SFunction<T, V> column);

    /**
     * Adds an exists condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param exists the expected existence flag
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> exists(SFunction<T, V> column, boolean exists);

    /**
     * Adds an all-condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param values the values that must all be present
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> all(SFunction<T, V> column, Collection<?> values);

    /**
     * Adds a size condition using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param size   the expected collection size
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> size(SFunction<T, V> column, int size);

    /**
     * Adds an ascending sort using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> orderByAsc(SFunction<T, V> column);

    /**
     * Adds a descending sort using a lambda field reference.
     *
     * @param column the lambda field reference
     * @param <V>    the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> orderByDesc(SFunction<T, V> column);

    /**
     * Adds a sort definition using a lambda field reference.
     *
     * @param column    the lambda field reference
     * @param ascending whether the sort should be ascending
     * @param <V>       the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> orderBy(SFunction<T, V> column, boolean ascending);

    /**
     * Restricts the selected fields using lambda field references.
     *
     * @param columns the included field references
     * @param <V>     the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> select(SFunction<T, V>... columns);

    /**
     * Excludes fields from the selected projection using lambda field references.
     *
     * @param columns the excluded field references
     * @param <V>     the field type
     * @return this query facade
     */
    <V> LambdaEngineQuery<T> exclude(SFunction<T, V>... columns);
}
