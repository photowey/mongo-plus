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
package io.github.photowey.mongoplus.executor;

import java.io.Serializable;
import java.util.List;

import io.github.photowey.mongoplus.executor.batch.BatchResult;

/**
 * BatchExecutor - Executes batch operations against MongoDB.
 * Uses BulkOperations for efficient batch processing.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface BatchExecutor {

    /**
     * Batch insert entities.
     *
     * @param entities       the entities to insert
     * @param collectionName the collection name
     * @param <T>            the entity type
     * @return batch result with counts
     */
    <T> BatchResult insertBatch(List<T> entities, String collectionName);

    /**
     * Batch insert entities (using entity class to determine collection).
     *
     * @param entities    the entities to insert
     * @param entityClass the entity class
     * @param <T>         the entity type
     * @return batch result with counts
     */
    <T> BatchResult insertBatch(List<T> entities, Class<T> entityClass);

    /**
     * Batch update entities by ID.
     *
     * @param entities       the entities to update (must have ID field)
     * @param collectionName the collection name
     * @param <T>            the entity type
     * @return batch result with counts
     */
    <T> BatchResult updateBatch(List<T> entities, String collectionName);

    /**
     * Batch update entities by ID (using entity class).
     *
     * @param entities    the entities to update
     * @param entityClass the entity class
     * @param <T>         the entity type
     * @return batch result with counts
     */
    <T> BatchResult updateBatch(List<T> entities, Class<T> entityClass);

    /**
     * Batch delete entities by IDs.
     *
     * @param ids            the IDs to delete
     * @param collectionName the collection name
     * @return batch result with counts
     */
    BatchResult deleteBatch(List<? extends Serializable> ids, String collectionName);

    /**
     * Batch delete entities by IDs (using entity class).
     *
     * @param ids         the IDs to delete
     * @param entityClass the entity class
     * @return batch result with counts
     */
    <T> BatchResult deleteBatch(List<? extends Serializable> ids, Class<T> entityClass);
}
