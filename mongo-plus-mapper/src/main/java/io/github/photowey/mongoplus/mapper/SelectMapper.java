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
import java.util.List;

import io.github.photowey.mongoplus.core.page.Page;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.LambdaQueryWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;

/**
 * MapperSelect - Select/query operations for MongoMapper.
 *
 * <p>Example:</p>
 * <pre>{@code
 * User user = userMapper.selectById("1");
 * List<User> list = userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getStatus, 1));
 * long total = userMapper.selectCount(wrapper);
 * Page<User> page = userMapper.selectPage(Page.of(1, 10), wrapper);
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface SelectMapper<T> {

    /**
     * Select one entity by ID.
     *
     * @param id the entity id (e.g. ObjectId string or Long)
     * @return the entity or null if not found
     */
    T selectById(Serializable id);

    /**
     * Select list by QueryWrapper.
     *
     * @param wrapper the query wrapper
     * @return list of entities, never null
     */
    List<T> selectList(QueryWrapper<T> wrapper);

    /**
     * Select list by LambdaQueryWrapper.
     *
     * @param wrapper the lambda query wrapper
     * @return list of entities, never null
     */
    List<T> selectList(LambdaQueryWrapper<T> wrapper);

    /**
     * Select one entity by wrapper. Throws or returns null when multiple results exist (implementation-defined).
     *
     * @param wrapper the query wrapper
     * @return the single entity or null
     */
    T selectOne(QueryWrapper<T> wrapper);

    /**
     * Select one entity by LambdaQueryWrapper.
     *
     * @param wrapper the lambda query wrapper
     * @return the single entity or null
     */
    T selectOne(LambdaQueryWrapper<T> wrapper);

    /**
     * Count records matching the wrapper.
     *
     * @param wrapper the query wrapper
     * @return total count
     */
    long selectCount(AbstractWrapper<T> wrapper);

    /**
     * Check if any record exists matching the wrapper.
     *
     * @param wrapper the query wrapper
     * @return true if at least one record exists
     */
    boolean exists(AbstractWrapper<T> wrapper);

    /**
     * Select paginated records by QueryWrapper.
     *
     * @param page    the pagination (page number and size)
     * @param wrapper the query wrapper
     * @return page with records and total count
     */
    Page<T> selectPage(Page<T> page, QueryWrapper<T> wrapper);

    /**
     * Select paginated records by LambdaQueryWrapper.
     *
     * @param page    the pagination (page number and size)
     * @param wrapper the lambda query wrapper
     * @return page with records and total count
     */
    Page<T> selectPage(Page<T> page, LambdaQueryWrapper<T> wrapper);
}
