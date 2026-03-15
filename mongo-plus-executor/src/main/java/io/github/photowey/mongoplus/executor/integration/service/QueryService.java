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

import io.github.photowey.mongoplus.executor.integration.query.EngineQuery;
import io.github.photowey.mongoplus.executor.integration.query.LambdaEngineQuery;

/**
 * QueryService - Service boundary for creating executable query objects.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/10
 */
public interface QueryService {

    /**
     * Create a metadata-oriented executable query.
     *
     * @param entityClass the entity class
     * @param <T>         the entity type
     * @return the executable query
     */
    <T> EngineQuery<T> createQuery(Class<T> entityClass);

    /**
     * Create a lambda-oriented executable query.
     *
     * @param entityClass the entity class
     * @param <T>         the entity type
     * @return the executable lambda query
     */
    <T> LambdaEngineQuery<T> createLambdaQuery(Class<T> entityClass);
}
