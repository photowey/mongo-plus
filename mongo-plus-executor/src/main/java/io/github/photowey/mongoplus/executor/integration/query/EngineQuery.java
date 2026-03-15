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

import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.dsl.support.QueryDSL;

/**
 * EngineQuery - Defines an executable query facade that combines fluent condition building with terminal operations.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * List<UserDocument> users = engineQuery
 *     .eq("status", 1)
 *     .orderByDesc("createdAt")
 *     .limit(20L)
 *     .list();
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public interface EngineQuery<T> extends QueryDSL<T, EngineQuery<T>> {

    /**
     * Adds an equality condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return this query facade
     */
    EngineQuery<T> eq(String field, Object value);

    /**
     * Adds an equality condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> eq(FieldMetadata<V> field, V value);

    /**
     * Adds an inequality condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return this query facade
     */
    EngineQuery<T> ne(String field, Object value);

    /**
     * Adds an inequality condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> ne(FieldMetadata<V> field, V value);

    /**
     * Adds a greater-than condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return this query facade
     */
    EngineQuery<T> gt(String field, Object value);

    /**
     * Adds a greater-than condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> gt(FieldMetadata<V> field, V value);

    /**
     * Adds a greater-than-or-equal condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return this query facade
     */
    EngineQuery<T> gte(String field, Object value);

    /**
     * Adds a greater-than-or-equal condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> gte(FieldMetadata<V> field, V value);

    /**
     * Adds a less-than condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return this query facade
     */
    EngineQuery<T> lt(String field, Object value);

    /**
     * Adds a less-than condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> lt(FieldMetadata<V> field, V value);

    /**
     * Adds a less-than-or-equal condition using a raw field name.
     *
     * @param field the field name
     * @param value the comparison value
     * @return this query facade
     */
    EngineQuery<T> lte(String field, Object value);

    /**
     * Adds a less-than-or-equal condition using field metadata.
     *
     * @param field the field metadata
     * @param value the comparison value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> lte(FieldMetadata<V> field, V value);

    /**
     * Adds a between condition using a raw field name.
     *
     * @param field the field name
     * @param min   the inclusive lower bound
     * @param max   the inclusive upper bound
     * @return this query facade
     */
    EngineQuery<T> between(String field, Object min, Object max);

    /**
     * Adds a between condition using field metadata.
     *
     * @param field the field metadata
     * @param min   the inclusive lower bound
     * @param max   the inclusive upper bound
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> between(FieldMetadata<V> field, V min, V max);

    /**
     * Adds an in-condition using a raw field name and a collection.
     *
     * @param field  the field name
     * @param values the accepted values
     * @return this query facade
     */
    EngineQuery<T> in(String field, Collection<?> values);

    /**
     * Adds an in-condition using a raw field name and a varargs value list.
     *
     * @param field  the field name
     * @param values the accepted values
     * @return this query facade
     */
    EngineQuery<T> in(String field, Object... values);

    /**
     * Adds an in-condition using field metadata and a collection.
     *
     * @param field  the field metadata
     * @param values the accepted values
     * @return this query facade
     */
    EngineQuery<T> in(FieldMetadata<?> field, Collection<?> values);

    /**
     * Adds an in-condition using field metadata and a varargs value list.
     *
     * @param field  the field metadata
     * @param values the accepted values
     * @return this query facade
     */
    EngineQuery<T> in(FieldMetadata<?> field, Object... values);

    /**
     * Adds a not-in condition using a raw field name.
     *
     * @param field  the field name
     * @param values the rejected values
     * @return this query facade
     */
    EngineQuery<T> notIn(String field, Collection<?> values);

    /**
     * Adds a not-in condition using field metadata.
     *
     * @param field  the field metadata
     * @param values the rejected values
     * @return this query facade
     */
    EngineQuery<T> notIn(FieldMetadata<?> field, Collection<?> values);

    /**
     * Alias for the not-in condition using a raw field name.
     *
     * @param field  the field name
     * @param values the rejected values
     * @return this query facade
     */
    EngineQuery<T> nin(String field, Collection<?> values);

    /**
     * Alias for the not-in condition using field metadata.
     *
     * @param field  the field metadata
     * @param values the rejected values
     * @return this query facade
     */
    EngineQuery<T> nin(FieldMetadata<?> field, Collection<?> values);

    /**
     * Adds a like condition using a raw field name.
     *
     * @param field the field name
     * @param value the pattern source value
     * @return this query facade
     */
    EngineQuery<T> like(String field, Object value);

    /**
     * Adds a like condition using field metadata.
     *
     * @param field the field metadata
     * @param value the pattern source value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> like(FieldMetadata<V> field, V value);

    /**
     * Adds a left-like condition using a raw field name.
     *
     * @param field the field name
     * @param value the pattern source value
     * @return this query facade
     */
    EngineQuery<T> likeLeft(String field, Object value);

    /**
     * Adds a left-like condition using field metadata.
     *
     * @param field the field metadata
     * @param value the pattern source value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> likeLeft(FieldMetadata<V> field, V value);

    /**
     * Adds a right-like condition using a raw field name.
     *
     * @param field the field name
     * @param value the pattern source value
     * @return this query facade
     */
    EngineQuery<T> likeRight(String field, Object value);

    /**
     * Adds a right-like condition using field metadata.
     *
     * @param field the field metadata
     * @param value the pattern source value
     * @param <V>   the field type
     * @return this query facade
     */
    <V> EngineQuery<T> likeRight(FieldMetadata<V> field, V value);

    /**
     * Adds a regex condition using a raw field name.
     *
     * @param field   the field name
     * @param pattern the regex pattern
     * @return this query facade
     */
    EngineQuery<T> regex(String field, String pattern);

    /**
     * Adds a regex condition using field metadata.
     *
     * @param field   the field metadata
     * @param pattern the regex pattern
     * @return this query facade
     */
    EngineQuery<T> regex(FieldMetadata<?> field, String pattern);

    /**
     * Adds an is-null condition using a raw field name.
     *
     * @param field the field name
     * @return this query facade
     */
    EngineQuery<T> isNull(String field);

    /**
     * Adds an is-null condition using field metadata.
     *
     * @param field the field metadata
     * @return this query facade
     */
    EngineQuery<T> isNull(FieldMetadata<?> field);

    /**
     * Adds an is-not-null condition using a raw field name.
     *
     * @param field the field name
     * @return this query facade
     */
    EngineQuery<T> isNotNull(String field);

    /**
     * Adds an is-not-null condition using field metadata.
     *
     * @param field the field metadata
     * @return this query facade
     */
    EngineQuery<T> isNotNull(FieldMetadata<?> field);

    /**
     * Adds an exists condition using a raw field name.
     *
     * @param field  the field name
     * @param exists the expected existence flag
     * @return this query facade
     */
    EngineQuery<T> exists(String field, boolean exists);

    /**
     * Adds an exists condition using field metadata.
     *
     * @param field  the field metadata
     * @param exists the expected existence flag
     * @return this query facade
     */
    EngineQuery<T> exists(FieldMetadata<?> field, boolean exists);

    /**
     * Executes the query and checks whether at least one document matches.
     *
     * @return {@code true} when at least one document matches
     */
    boolean exists();

    /**
     * Adds an all-condition using a raw field name.
     *
     * @param field  the field name
     * @param values the values that must all be present
     * @return this query facade
     */
    EngineQuery<T> all(String field, Collection<?> values);

    /**
     * Adds an all-condition using field metadata.
     *
     * @param field  the field metadata
     * @param values the values that must all be present
     * @return this query facade
     */
    EngineQuery<T> all(FieldMetadata<?> field, Collection<?> values);

    /**
     * Adds a size condition using a raw field name.
     *
     * @param field the field name
     * @param size  the expected collection size
     * @return this query facade
     */
    EngineQuery<T> size(String field, int size);

    /**
     * Adds a size condition using field metadata.
     *
     * @param field the field metadata
     * @param size  the expected collection size
     * @return this query facade
     */
    EngineQuery<T> size(FieldMetadata<?> field, int size);

    /**
     * Adds a nested conjunction block.
     *
     * @param consumer the callback that fills the nested block
     * @return this query facade
     */
    EngineQuery<T> and(Consumer<EngineQuery<T>> consumer);

    /**
     * Adds a nested disjunction block.
     *
     * @param consumer the callback that fills the nested block
     * @return this query facade
     */
    EngineQuery<T> or(Consumer<EngineQuery<T>> consumer);

    /**
     * Executes the query and returns a single document.
     *
     * @return the matched document, or {@code null} when none matches
     */
    T one();

    /**
     * Executes the query and returns the result list.
     *
     * @return the matched documents
     */
    List<T> list();

    /**
     * Executes the query and returns the matching document count.
     *
     * @return the matching document count
     */
    long count();

    /**
     * Executes the query using the default page settings.
     *
     * @return the paged result
     */
    Page<T> page();

    /**
     * Executes the query for the supplied page number and the default page size.
     *
     * @param current the current page number
     * @return the paged result
     */
    Page<T> page(long current);

    /**
     * Executes the query for the supplied page number and page size.
     *
     * @param current the current page number
     * @param size    the page size
     * @return the paged result
     */
    Page<T> page(long current, long size);
}
