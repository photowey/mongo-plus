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
package io.github.photowey.mongoplus.executor.template.impl;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.github.photowey.mongoplus.executor.aware.MongoTemplateAware;

/**
 * AbstractMongoTemplateExecutor - Base executor that stores the active MongoTemplate.
 *
 * @author photowey
 * @version 2026.1.0.0
 * @since 2026/03/07
 */
public abstract class AbstractMongoTemplateExecutor implements MongoTemplateAware {

    protected MongoTemplate mongoTemplate;

    @Override
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoTemplate mongoTemplate() {
        return this.mongoTemplate;
    }
}
