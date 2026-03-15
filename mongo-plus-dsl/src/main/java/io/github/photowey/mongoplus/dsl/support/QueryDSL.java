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
package io.github.photowey.mongoplus.dsl.support;

import io.github.photowey.mongoplus.core.metadata.FieldMetadata;
import io.github.photowey.mongoplus.core.util.Arrays;
import io.github.photowey.mongoplus.core.util.Objects;

/**
 * QueryDSL - Defines projection, sort, and pagination configuration shared by query-oriented APIs.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * QueryWrapper<UserDocument> wrapper = Wrappers.query(UserDocument.class)
 *     .select("userName", "age")
 *     .orderByDesc("createdAt")
 *     .paginate(1L, 20L);
 * }</pre>
 *
 * @param <T> the entity type
 * @param <R> the fluent return type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/15
 */
public interface QueryDSL<T, R extends QueryDSL<T, R>> {

    /**
     * Includes the supplied fields in the projection.
     *
     * @param fields the included field names
     * @return the current DSL instance
     */
    R select(String... fields);

    /**
     * Includes the supplied fields in the projection via field metadata.
     *
     * @param fields the included field metadata values
     * @return the current DSL instance
     */
    default R select(FieldMetadata<?>... fields) {
        if (Arrays.isEmpty(fields)) {
            return this.select(new String[0]);
        }

        String[] resolved = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            resolved[i] = this.resolveField(fields[i]);
        }

        return this.select(resolved);
    }

    /**
     * Excludes the supplied fields from the projection.
     *
     * @param fields the excluded field names
     * @return the current DSL instance
     */
    R exclude(String... fields);

    /**
     * Excludes the supplied fields from the projection via field metadata.
     *
     * @param fields the excluded field metadata values
     * @return the current DSL instance
     */
    default R exclude(FieldMetadata<?>... fields) {
        if (Arrays.isEmpty(fields)) {
            return this.exclude(new String[0]);
        }

        String[] resolved = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            resolved[i] = this.resolveField(fields[i]);
        }

        return this.exclude(resolved);
    }

    /**
     * Adds a sort definition using a raw field name.
     *
     * @param ascending whether the sort should be ascending
     * @param field the field name
     * @return the current DSL instance
     */
    R orderBy(boolean ascending, String field);

    /**
     * Adds a sort definition using field metadata.
     *
     * @param ascending whether the sort should be ascending
     * @param field the field metadata
     * @return the current DSL instance
     */
    default R orderBy(boolean ascending, FieldMetadata<?> field) {
        return this.orderBy(ascending, this.resolveField(field));
    }

    /**
     * Adds an ascending sort definition using a raw field name.
     *
     * @param field the field name
     * @return the current DSL instance
     */
    default R orderByAsc(String field) {
        return this.orderBy(true, field);
    }

    /**
     * Adds an ascending sort definition using field metadata.
     *
     * @param field the field metadata
     * @return the current DSL instance
     */
    default R orderByAsc(FieldMetadata<?> field) {
        return this.orderBy(true, field);
    }

    /**
     * Adds a descending sort definition using a raw field name.
     *
     * @param field the field name
     * @return the current DSL instance
     */
    default R orderByDesc(String field) {
        return this.orderBy(false, field);
    }

    /**
     * Adds a descending sort definition using field metadata.
     *
     * @param field the field metadata
     * @return the current DSL instance
     */
    default R orderByDesc(FieldMetadata<?> field) {
        return this.orderBy(false, field);
    }

    /**
     * Limits the maximum result size.
     *
     * @param limit the maximum result size
     * @return the current DSL instance
     */
    R limit(long limit);

    /**
     * Skips the supplied number of documents.
     *
     * @param skip the number of documents to skip
     * @return the current DSL instance
     */
    R skip(long skip);

    /**
     * Applies page-based pagination settings to the current query state.
     *
     * @param pageNum the current page number, starting from {@code 1}
     * @param pageSize the page size
     * @return the current DSL instance
     */
    R paginate(long pageNum, long pageSize);

    /**
     * Resolves a persisted field path from field metadata.
     *
     * @param fieldMetadata the field metadata
     * @return the resolved field path
     */
    default String resolveField(FieldMetadata<?> fieldMetadata) {
        if (Objects.isNull(fieldMetadata)) {
            throw new IllegalArgumentException("Field metadata must not be null");
        }

        return fieldMetadata.resolvePath();
    }
}
