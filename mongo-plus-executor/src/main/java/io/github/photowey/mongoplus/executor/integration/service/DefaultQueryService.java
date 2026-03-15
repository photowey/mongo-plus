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
package io.github.photowey.mongoplus.executor.integration.service;

import io.github.photowey.mongoplus.executor.QueryExecutor;
import io.github.photowey.mongoplus.executor.integration.query.DefaultEngineQuery;
import io.github.photowey.mongoplus.executor.integration.query.DefaultLambdaEngineQuery;
import io.github.photowey.mongoplus.executor.integration.query.EngineQuery;
import io.github.photowey.mongoplus.executor.integration.query.LambdaEngineQuery;

/**
 * DefaultQueryService - Default query service implementation.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public class DefaultQueryService implements QueryService {

    private final QueryExecutor queryExecutor;

    public DefaultQueryService(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    @Override
    public <T> EngineQuery<T> createQuery(Class<T> entityClass) {
        return new DefaultEngineQuery<>(this.queryExecutor, entityClass);
    }

    @Override
    public <T> LambdaEngineQuery<T> createLambdaQuery(Class<T> entityClass) {
        return new DefaultLambdaEngineQuery<>(this.queryExecutor, entityClass);
    }
}
