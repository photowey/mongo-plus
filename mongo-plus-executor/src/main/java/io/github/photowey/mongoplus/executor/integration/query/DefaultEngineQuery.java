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

import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.wrapper.AbstractWrapper;
import io.github.photowey.mongoplus.wrapper.QueryWrapper;

/**
 * DefaultEngineQuery - Metadata-oriented executable query implementation.
 *
 * @param <T> the entity type
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public class DefaultEngineQuery<T>
    extends AbstractEngineQuery<T, QueryWrapper<T>, EngineQuery<T>> {

    public DefaultEngineQuery(QueryExecutor queryExecutor, Class<T> entityClass) {
        this(queryExecutor, entityClass, DefaultEngineQuery.newWrapper(entityClass));
    }

    private DefaultEngineQuery(QueryExecutor queryExecutor, Class<T> entityClass, QueryWrapper<T> wrapper) {
        super(queryExecutor, entityClass, wrapper);
    }

    @Override
    protected EngineQuery<T> self() {
        return this;
    }

    @Override
    protected EngineQuery<T> newQuery(QueryWrapper<T> wrapper) {
        return new DefaultEngineQuery<>(this.queryExecutor, this.entityClass, wrapper);
    }

    @Override
    protected QueryWrapper<T> castWrapper(AbstractWrapper<T> wrapper) {
        return (QueryWrapper<T>) wrapper;
    }

    private static <T> QueryWrapper<T> newWrapper(Class<T> entityClass) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        wrapper.setEntityClass(entityClass);
        return wrapper;
    }
}
