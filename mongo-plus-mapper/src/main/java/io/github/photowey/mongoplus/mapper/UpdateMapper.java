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
package io.github.photowey.mongoplus.mapper;

import io.github.photowey.mongoplus.wrapper.AbstractWrapper;

/**
 * UpdateMapper - Update operations for MongoMapper.
 *
 * <p>Example:</p>
 * <pre>{@code
 * user.setName("Bob");
 * userMapper.updateById(user);
 * userMapper.update(entity, Wrappers.<User>lambdaUpdate().eq(User::getId, id).set(User::getStatus, 2));
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface UpdateMapper<T> {

    /**
     * Update by entity ID. Uses the entity's ID field to match and updates other fields.
     *
     * @param entity the entity with ID and fields to update
     * @return true if a document was updated
     */
    boolean updateById(T entity);

    /**
     * Update by wrapper (e.g. LambdaUpdateWrapper with set/eq conditions).
     *
     * @param entity  the entity or null when wrapper carries all update data
     * @param wrapper the update wrapper
     * @return true if at least one document was updated
     */
    boolean update(T entity, AbstractWrapper<T> wrapper);
}
