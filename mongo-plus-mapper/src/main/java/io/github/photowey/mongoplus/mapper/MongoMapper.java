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

import java.util.List;

import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;

/**
 * MongoMapper - Base mapper interface for MongoDB operations.
 * Unifies select, insert, update, delete, batch and aggregate capabilities.
 *
 * <p>Example:</p>
 * <pre>{@code
 * public interface UserMapper extends MongoMapper<User> {}
 * List<User> list = userMapper.list(Wrappers.lambdaQuery(User.class).eq(User::getStatus, 1));
 * long n = userMapper.count(wrapper);
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface MongoMapper<T> extends Mapper<T>,
    SelectMapper<T>,
    InsertMapper<T>,
    UpdateMapper<T>,
    DeleteMapper<T>,
    BatchMapper<T>,
    AggregateMapper<T> {

    /**
     * Alias for {@link SelectMapper#selectList(QueryWrapper)}.
     */
    default List<T> list(QueryWrapper<T> wrapper) {
        return this.selectList(wrapper);
    }

    /**
     * Alias for {@link SelectMapper#selectList(LambdaQueryWrapper)}.
     */
    default List<T> list(LambdaQueryWrapper<T> wrapper) {
        return this.selectList(wrapper);
    }

    /**
     * Alias for {@link SelectMapper#selectOne(QueryWrapper)}.
     */
    default T one(QueryWrapper<T> wrapper) {
        return this.selectOne(wrapper);
    }

    /**
     * Alias for {@link SelectMapper#selectOne(LambdaQueryWrapper)}.
     */
    default T one(LambdaQueryWrapper<T> wrapper) {
        return this.selectOne(wrapper);
    }

    /**
     * Alias for {@link SelectMapper#selectCount(AbstractWrapper)}.
     */
    default long count(AbstractWrapper<T> wrapper) {
        return this.selectCount(wrapper);
    }
}
