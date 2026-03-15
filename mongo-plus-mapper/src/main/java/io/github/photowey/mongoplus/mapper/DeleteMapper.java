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

import java.io.Serializable;

import io.github.photowey.mongoplus.wrapper.AbstractWrapper;

/**
 * DeleteMapper - Delete operations for MongoMapper.
 *
 * <p>Example:</p>
 * <pre>{@code
 * userMapper.deleteById("1");
 * userMapper.delete(Wrappers.<User>lambdaQuery().eq(User::getStatus, 0));
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface DeleteMapper<T> {

    /**
     * Delete one document by ID.
     *
     * @param id the document id
     * @return true if a document was deleted
     */
    boolean deleteById(Serializable id);

    /**
     * Delete documents matching the wrapper.
     *
     * @param wrapper the query wrapper
     * @return true if at least one document was deleted
     */
    boolean delete(AbstractWrapper<T> wrapper);
}
