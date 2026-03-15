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

import io.github.photowey.mongoplus.aggregation.stage.wrapper.AggregationWrapper;

/**
 * AggregateMapper - Aggregation operations for MongoMapper.
 *
 * <p>Example:</p>
 * <pre>{@code
 * List<DeptStat> list = userMapper.aggregate(
 *     AggregationWrapper.aggregation(User.class).group("dept").count("cnt"),
 *     DeptStat.class
 * );
 * }</pre>
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/12
 */
public interface AggregateMapper<T> {

    /**
     * Execute aggregation pipeline and map results to output type.
     *
     * @param wrapper     the aggregation wrapper (match, group, project, etc.)
     * @param outputClass the result type
     * @param <O>         the output element type
     * @return list of aggregation results, never null
     */
    <O> List<O> aggregate(AggregationWrapper<T> wrapper, Class<O> outputClass);
}
