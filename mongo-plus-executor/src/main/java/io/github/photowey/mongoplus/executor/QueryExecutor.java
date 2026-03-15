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

import java.util.List;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.UpdateWrapper;

/**
 * QueryExecutor - Executes queries against MongoDB.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface QueryExecutor extends BatchExecutor, SimpleExecutor {

    <T> List<T> selectList(AbstractWrapper<T> wrapper, Class<T> entityClass);

    <T> T selectOne(AbstractWrapper<T> wrapper, Class<T> entityClass);

    long selectCount(AbstractWrapper<?> wrapper, String collectionName);

    <T> boolean exists(AbstractWrapper<T> wrapper, Class<T> entityClass);

    <T> boolean delete(AbstractWrapper<T> wrapper, Class<T> entityClass);

    /**
     * Update entities matching the wrapper conditions with SET/INC values.
     * UpdateWrapper itself contains WHERE conditions (inherited from AbstractWrapper).
     * If updateWrapper has no SET/INC values, returns false without executing MongoDB operation.
     *
     * @param updateWrapper the update wrapper with WHERE conditions and SET/INC values
     * @param entityClass   the entity class
     * @param <T>           the entity type
     * @return true if any document was updated, false if no updates or no matches
     */
    <T> boolean update(UpdateWrapper<T> updateWrapper, Class<T> entityClass);

    /**
     * Select paginated records matching the wrapper conditions.
     * Automatically executes count query if page.searchCount is true.
     *
     * @param page        the pagination info (current, size, searchCount)
     * @param wrapper     the query wrapper
     * @param entityClass the entity class
     * @param <T>         the entity type
     * @return Page with records and total count
     */
    <T> Page<T> selectPage(Page<T> page, AbstractWrapper<T> wrapper, Class<T> entityClass);
}
