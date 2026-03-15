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

/**
 * InsertMapper - Insert operations for MongoMapper.
 *
 * <p>Example:</p>
 * <pre>{@code
 * User user = new User();
 * user.setName("Alice");
 * boolean ok = userMapper.insert(user);
 * // user.getId() is populated after insert
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface InsertMapper<T> {

    /**
     * Insert one entity. The entity's ID field will be populated after successful insertion.
     *
     * @param entity the entity to insert
     * @return true if insertion was successful
     */
    boolean insert(T entity);
}
